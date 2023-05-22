package cn.flood.websocket.endpoint;


import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

  @Override
  public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request,
      HandshakeResponse response) {
    HttpSession httpSession = (HttpSession) request.getHttpSession();
    sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    super.modifyHandshake(sec, request, response);
  }
}
