/**
 * Copyright (c) 2018-2028,
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.flood.cloud.http;

import okhttp3.*;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * {@linkplain OkHttpClient#interceptors() application interceptor} or as a {@linkplain
 * OkHttpClient#networkInterceptors() network interceptor}. <p> The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 * a stable logging format, use your own interceptor.
 *
 * @author mmdai
 */
public final class HttpLoggingInterceptor implements Interceptor {
	private static final Charset UTF8 = StandardCharsets.UTF_8;

	public enum Level {
		/**
		 * No logs.
		 */
		NONE,
		/**
		 * Logs request and response lines.
		 *
		 * <p>Example:
		 * <pre>{@code
		 * --> POST /greeting http/1.1 (3-byte body)
		 *
		 * <-- 200 OK (22ms, 6-byte body)
		 * }</pre>
		 */
		BASIC,
		/**
		 * Logs request and response lines and their respective headers.
		 *
		 * <p>Example:
		 * <pre>{@code
		 * --> POST /greeting http/1.1
		 * Host: example.com
		 * Content-Type: plain/text
		 * Content-Length: 3
		 * --> END POST
		 *
		 * <-- 200 OK (22ms)
		 * Content-Type: plain/text
		 * Content-Length: 6
		 * <-- END HTTP
		 * }</pre>
		 */
		HEADERS,
		/**
		 * Logs request and response lines and their respective headers and bodies (if present).
		 *
		 * <p>Example:
		 * <pre>{@code
		 * --> POST /greeting http/1.1
		 * Host: example.com
		 * Content-Type: plain/text
		 * Content-Length: 3
		 *
		 * Hi?
		 * --> END POST
		 *
		 * <-- 200 OK (22ms)
		 * Content-Type: plain/text
		 * Content-Length: 6
		 *
		 * Hello!
		 * <-- END HTTP
		 * }</pre>
		 */
		BODY
	}

	public interface Logger {
		/**
		 * log
		 * @param message message
		 */
		void log(String message);

		/**
		 * A {@link Logger} defaults output appropriate for the current platform.
		 */
		Logger DEFAULT = message -> Platform.get().log(INFO, message, null);
	}

	public HttpLoggingInterceptor() {
		this(Logger.DEFAULT);
	}

	public HttpLoggingInterceptor(Logger logger) {
		this.logger = logger;
	}

	private final Logger logger;

	private volatile Level level = Level.NONE;

	/**
	 * Change the level at which this interceptor logs.
	 * @param level log Level
	 * @return HttpLoggingInterceptor
	 */
	public HttpLoggingInterceptor setLevel(Level level) {
		Objects.requireNonNull(level, "level == null. Use Level.NONE instead.");
		this.level = level;
		return this;
	}

	public Level getLevel() {
		return level;
	}

	private String gzip = "gzip";
	private String contentEncoding = "Content-Encoding";

	@Override
	public Response intercept(Chain chain) throws IOException {
		Level level = this.level;

		Request request = chain.request();
		if (level == Level.NONE) {
			return chain.proceed(request);
		}

		boolean logBody = level == Level.BODY;
		boolean logHeaders = logBody || level == Level.HEADERS;

		RequestBody requestBody = request.body();
		boolean hasRequestBody = requestBody != null;

		Connection connection = chain.connection();
		StringBuilder requestStartMessage = new StringBuilder(300);
		requestStartMessage.append("\n");
		requestStartMessage.append(" ").append("===> ").append(request.method()).
				append(' ').append(request.url()).
				append((connection != null ? " " + connection.protocol() : ""));

		if (!logHeaders && hasRequestBody) {
			requestStartMessage.append(" ").append(" (").append(requestBody.contentLength()).append("-byte body");
		}
		requestStartMessage.append("\n");
		if (logHeaders) {
			if (hasRequestBody) {
				// Request body headers are only present when installed as a network interceptor. Force
				// them to be included (when available) so there values are known.
				if (requestBody.contentType() != null) {
					requestStartMessage.append(" ").append("Content-Type: ").append(requestBody.contentType()).append("\n");
				}
				if (requestBody.contentLength() != -1) {
					requestStartMessage.append(" ").append("Content-Length: ").append(requestBody.contentLength()).append("\n");
				}
			}

			Headers headers = request.headers();
			for (int i = 0, count = headers.size(); i < count; i++) {
				String name = headers.name(i);
				// Skip headers from the request body as they are explicitly logged above.
				if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
					requestStartMessage.append(" ").append(name).append(": ").append(headers.value(i)).append("\n");
				}
			}

			if (!logBody || !hasRequestBody) {
				requestStartMessage.append(" ").append("===>  END ").append(request.method()).append("\n");
			} else if (bodyHasUnknownEncoding(request.headers())) {
				requestStartMessage.append(" ").append("===>  END ").append(request.method()).append(" (encoded body omitted)").append("\n");
			} else {
				Buffer buffer = new Buffer();
				requestBody.writeTo(buffer);

				Charset charset = UTF8;
				MediaType contentType = requestBody.contentType();
				if (contentType != null) {
					charset = contentType.charset(UTF8);
				}

				requestStartMessage.append("\n");
				if (isPlaintext(buffer)) {
					requestStartMessage.append(" ").append(buffer.readString(charset)).append("\n");
					requestStartMessage.append(" ").append("===>  END ").append(request.method()).
							append(" (").append(requestBody.contentLength()).append("-byte body)").append("\n");
				} else {
					requestStartMessage.append(" ").append("===>  END ").append(request.method()).
							append(" (binary ").append(requestBody.contentLength()).append("-byte body omitted)").append("\n");
				}
			}
		}
		logger.log(requestStartMessage.toString());
		long startNs = System.nanoTime();
		Response response;
		try {
			response = chain.proceed(request);
		} catch (Exception e) {
			logger.log("<=== HTTP FAILED: " + e);
			throw e;
		}
		long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

		ResponseBody responseBody = response.body();
		long contentLength = responseBody.contentLength();
		String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
		StringBuilder responseEndMessage = new StringBuilder(300);
		responseEndMessage.append("\n");
		responseEndMessage.append("<=== ").append(response.code()).append(response.message().isEmpty() ? "" : ' ' + response.message()).
				append(' ').append(response.request().url()).append(" (").append(tookMs).append("ms").
				append(!logHeaders ? ", " + bodySize + " body" : "").append(')').append("\n");
		if (logHeaders) {
			Headers headers = response.headers();
			for (int i = 0, count = headers.size(); i < count; i++) {
				responseEndMessage.append(" ").append(headers.name(i)).append(": ").append(headers.value(i)).append("\n");
			}

			if (!logBody || !HttpHeaders.hasBody(response)) {
				responseEndMessage.append(" ").append("<=== END HTTP").append("\n");
			} else if (bodyHasUnknownEncoding(response.headers())) {
				responseEndMessage.append(" ").append("<=== END HTTP (encoded body omitted)").append("\n");
			} else {
				BufferedSource source = responseBody.source();
				// Buffer the entire body.
				source.request(Long.MAX_VALUE);
				Buffer buffer = source.buffer();

				Long gzippedLength = null;
				if (gzip.equalsIgnoreCase(headers.get(contentEncoding))) {
					gzippedLength = buffer.size();
					GzipSource gzippedResponseBody = null;
					try {
						gzippedResponseBody = new GzipSource(buffer.clone());
						buffer = new Buffer();
						buffer.writeAll(gzippedResponseBody);
					} finally {
						if (gzippedResponseBody != null) {
							gzippedResponseBody.close();
						}
					}
				}

				Charset charset = UTF8;
				MediaType contentType = responseBody.contentType();
				if (contentType != null) {
					charset = contentType.charset(UTF8);
				}

				if (!isPlaintext(buffer)) {
					responseEndMessage.append("\n");
					responseEndMessage.append("<=== END HTTP (binary ").append(buffer.size()).
							append("-byte body omitted)").append("\n");
					logger.log(responseEndMessage.toString());
					return response;
				}

				if (contentLength != 0) {
					responseEndMessage.append("\n");
					responseEndMessage.append(buffer.clone().readString(charset)).append("\n");
				}

				if (gzippedLength != null) {
					responseEndMessage.append("<=== END HTTP (").append(buffer.size()).
							append("-byte, ").append(gzippedLength).append("-gzipped-byte body)").append("\n");
				} else {
					responseEndMessage.append("<=== END HTTP (").append(buffer.size()).
							append("-byte body").append("\n");
				}
			}
		}
		logger.log(responseEndMessage.toString());
		return response;
	}

	/**
	 * Returns true if the body in question probably contains human readable text. Uses a small sample
	 * of code points to detect unicode control characters commonly used in binary file signatures.
	 */
	private static int plainCnt = 16;
	private static boolean isPlaintext(Buffer buffer) {
		try {
			Buffer prefix = new Buffer();
			long byteCount = buffer.size() < 64 ? buffer.size() : 64;
			buffer.copyTo(prefix, 0, byteCount);
			for (int i = 0; i < plainCnt; i++) {
				if (prefix.exhausted()) {
					break;
				}
				int codePoint = prefix.readUtf8CodePoint();
				if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
					return false;
				}
			}
			return true;
		} catch (EOFException e) {
			// Truncated UTF-8 sequence.
			return false;
		}
	}

	private boolean bodyHasUnknownEncoding(Headers headers) {
		String contentEncoding = headers.get("Content-Encoding");
		return contentEncoding != null
			&& !"identity".equalsIgnoreCase(contentEncoding)
			&& !"gzip".equalsIgnoreCase(contentEncoding);
	}
}
