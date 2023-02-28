package cn.flood.cloud.gateway.props;

import java.time.Duration;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * Properties used to configure resource handling.
 *
 * @author Phillip Webb
 * @author Brian Clozel
 * @author Dave Syer
 * @author Venil Noronha
 * @author Kristine Jetzke
 * @since 1.1.0 {@link WebProperties.Resources}
 */
@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
public class ResourceProperties extends WebProperties.Resources {

  private final ResourceProperties.Chain chain = new ResourceProperties.Chain();

  private final ResourceProperties.Cache cache = new ResourceProperties.Cache();

  @Override
  @DeprecatedConfigurationProperty(replacement = "spring.web.resources.static-locations")
  public String[] getStaticLocations() {
    return super.getStaticLocations();
  }

  @Override
  @DeprecatedConfigurationProperty(replacement = "spring.web.resources.add-mappings")
  public boolean isAddMappings() {
    return super.isAddMappings();
  }

  @Override
  public ResourceProperties.Chain getChain() {
    return this.chain;
  }

  @Override
  public ResourceProperties.Cache getCache() {
    return this.cache;
  }

  public static class Chain extends WebProperties.Resources.Chain {

    private final ResourceProperties.Strategy strategy = new ResourceProperties.Strategy();

    /**
     * Whether to enable HTML5 application cache manifest rewriting.
     */
    private boolean htmlApplicationCache = false;
    private boolean customized = false;

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.chain.enabled")
    public Boolean getEnabled() {
      return super.getEnabled();
    }

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.chain.cache")
    public boolean isCache() {
      return super.isCache();
    }

    @DeprecatedConfigurationProperty(reason = "The appcache manifest feature is being removed from browsers.")
    public boolean isHtmlApplicationCache() {
      return this.htmlApplicationCache;
    }

    public void setHtmlApplicationCache(boolean htmlApplicationCache) {
      this.htmlApplicationCache = htmlApplicationCache;
      this.customized = true;
    }

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.chain.compressed")
    public boolean isCompressed() {
      return super.isCompressed();
    }

    @Override
    public ResourceProperties.Strategy getStrategy() {
      return this.strategy;
    }

  }

  /**
   * Strategies for extracting and embedding a resource version in its URL path.
   */
  public static class Strategy extends WebProperties.Resources.Chain.Strategy {

    private final ResourceProperties.Fixed fixed = new ResourceProperties.Fixed();

    private final ResourceProperties.Content content = new ResourceProperties.Content();

    @Override
    public ResourceProperties.Fixed getFixed() {
      return this.fixed;
    }

    @Override
    public ResourceProperties.Content getContent() {
      return this.content;
    }

  }

  /**
   * Version Strategy based on content hashing.
   */
  public static class Content extends WebProperties.Resources.Chain.Strategy.Content {

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.chain.strategy.content.enabled")
    public boolean isEnabled() {
      return super.isEnabled();
    }

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.chain.strategy.content.paths")
    public String[] getPaths() {
      return super.getPaths();
    }

  }

  /**
   * Version Strategy based on a fixed version string.
   */
  public static class Fixed extends WebProperties.Resources.Chain.Strategy.Fixed {

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.chain.strategy.fixed.enabled")
    public boolean isEnabled() {
      return super.isEnabled();
    }

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.chain.strategy.fixed.paths")
    public String[] getPaths() {
      return super.getPaths();
    }

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.chain.strategy.fixed.version")
    public String getVersion() {
      return super.getVersion();
    }

  }

  /**
   * Cache configuration.
   */
  public static class Cache extends WebProperties.Resources.Cache {

    private final ResourceProperties.Cache.Cachecontrol cachecontrol = new ResourceProperties.Cache.Cachecontrol();

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.period")
    public Duration getPeriod() {
      return super.getPeriod();
    }

    @Override
    public ResourceProperties.Cache.Cachecontrol getCachecontrol() {
      return this.cachecontrol;
    }

    @Override
    @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.use-last-modified")
    public boolean isUseLastModified() {
      return super.isUseLastModified();
    }

    /**
     * Cache Control HTTP header configuration.
     */
    public static class Cachecontrol extends WebProperties.Resources.Cache.Cachecontrol {

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.max-age")
      public Duration getMaxAge() {
        return super.getMaxAge();
      }

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.no-cache")
      public Boolean getNoCache() {
        return super.getNoCache();
      }

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.no-store")
      public Boolean getNoStore() {
        return super.getNoStore();
      }

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.must-revalidate")
      public Boolean getMustRevalidate() {
        return super.getMustRevalidate();
      }

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.no-transform")
      public Boolean getNoTransform() {
        return super.getNoTransform();
      }

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.cache-public")
      public Boolean getCachePublic() {
        return super.getCachePublic();
      }

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.cache-private")
      public Boolean getCachePrivate() {
        return super.getCachePrivate();
      }

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.proxy-revalidate")
      public Boolean getProxyRevalidate() {
        return super.getProxyRevalidate();
      }

      @Override
      @DeprecatedConfigurationProperty(
          replacement = "spring.web.resources.cache.cachecontrol.stale-while-revalidate")
      public Duration getStaleWhileRevalidate() {
        return super.getStaleWhileRevalidate();
      }

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.stale-if-error")
      public Duration getStaleIfError() {
        return super.getStaleIfError();
      }

      @Override
      @DeprecatedConfigurationProperty(replacement = "spring.web.resources.cache.cachecontrol.s-max-age")
      public Duration getSMaxAge() {
        return super.getSMaxAge();
      }

    }

  }
}
