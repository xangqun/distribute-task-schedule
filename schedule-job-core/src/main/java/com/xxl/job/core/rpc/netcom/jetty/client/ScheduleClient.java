/**
 * Copyright 2017-2025 schedule Group.
 */
package com.xxl.job.core.rpc.netcom.jetty.client;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.rpc.codec.RpcRequest;
import com.xxl.job.core.rpc.codec.RpcResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;


/**
 * @author laixiangqun
 * @since 2018-6-29
 */
@Component("scheduleClient")
public class ScheduleClient {
    protected final Logger logger = LoggerFactory.getLogger(ScheduleClient.class);

//    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${egsc.schedule.ssl.enabled:false}")
    private String sslEnabled;

    @Value("${egsc.schedule.ssl.pfxpath:UNKOWN}")
    private String pfxpath;

    @Value("${egsc.schedule.ssl.pfxpwd:UNKOWN}")
    private String pfxpwd;

    @Value("${egsc.comfig.api.client.maxTotal:200}")
    private int clientMaxTotal;

    @Value("${egsc.comfig.api.client.defaultMaxPerRoute:100}")
    private int clientDefaultMaxPerRoute;

    @Value("${egsc.config.api.client.clientRequestTimeout:2000}")
    private int clientConnectRequestTimeout;

    @Value("${egsc.config.api.client.clientRequestSocketTimeout:10000}")
    private int clientRequestSocketTimeout;

    @Value("${egsc.config.api.client.readTimeout:10000}")
    private int clientReadTimeout;

    @Value("${egsc.config.api.client.connectTimeout:5000}")
    private int clientConnectTimeout;

    @Value("${egsc.config.api.client.retryCount:2}")
    private int retryCount;

    @Value("${logging.logtoELK:false}")
    private boolean logtoELK;

    @Value("${schedule.context-path:/}")
    private String contextPath;

    @PostConstruct
    public void init(){
        //初始化客户端
        initRestTemplate();
    }


    /**
     *
     * 初始化restTemplate相关配置
     *
     */
    private void initRestTemplate(){
        boolean flag = false;
        try {
            if ("true".equals(this.sslEnabled)) {
                CloseableHttpClient httpClient = this.acceptsUntrustedCertsHttpClient();
                if (null != httpClient) {
                    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
                    clientHttpRequestFactory.setReadTimeout(this.clientReadTimeout);
                    clientHttpRequestFactory.setConnectTimeout(this.clientConnectTimeout);
                    this.restTemplate = new RestTemplate(clientHttpRequestFactory);
                    flag = true;
                }
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | IOException | KeyManagementException e) {
            this.logger.error("实例化RestTemplate异常", e);
        }

        if (!flag) {
            this.restTemplate = this.createHttpClientRestTemplate();
        }
    }

    private RestTemplate createHttpClientRestTemplate(){
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager();
        // 总连接数
        pollingConnectionManager.setMaxTotal(this.clientMaxTotal);
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(this.clientDefaultMaxPerRoute);

        HttpClientBuilder httpClientBuilder = HttpClients.custom().disableRedirectHandling();
        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        // 重试次数，默认是3次，没有开启
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(this.retryCount, true));
        // 保持长连接配置，需要在头添加Keep-Alive
        // httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
        // 发送请求相关配置
    /*RequestConfig.Builder builder = RequestConfig.custom();
    builder.setConnectionRequestTimeout(this.clientConnectRequestTimeout);
    builder.setConnectTimeout(this.clientConnectTimeout);
    builder.setSocketTimeout(this.clientRequestSocketTimeout);

    RequestConfig requestConfig = builder.build();
    httpClientBuilder.setDefaultRequestConfig(requestConfig);*/
        CloseableHttpClient httpClient = httpClientBuilder.build();

        // httpClient连接配置，底层是配置RequestConfig
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        /* 连接超时
         * 这定义了通过网络与服务器建立连接的超时时间。
         * Httpclient包中通过一个异步线程去创建与服务器的socket连接，这就是该socket连接的超时时间，
         * 假设：访问一个IP，192.168.10.100，这个IP不存在或者响应太慢，那么将会返回
         * java.net.SocketTimeoutException: connect timed out
         */
        clientHttpRequestFactory.setConnectTimeout(this.clientConnectTimeout);
        /* 数据读取超时时间，即SocketTimeout
         * 指的是连接上一个url，获取response的返回等待时间，假设：url程序中存在阻塞、或者response
         * 返回的文件内容太大，在指定的时间内没有读完，则出现
         * java.net.SocketTimeoutException: Read timed out
         */
        clientHttpRequestFactory.setReadTimeout(this.clientReadTimeout);
        /* 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
         * 从连接池中获取连接的超时时间，假设：连接池中已经使用的连接数等于setMaxTotal，新来的线程在等待1*1000
         * 后超时，错误内容：org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
         */
        clientHttpRequestFactory.setConnectionRequestTimeout(this.clientConnectRequestTimeout);
        // 缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
        // clientHttpRequestFactory.setBufferRequestBody(false);

        return new RestTemplate(clientHttpRequestFactory);
    }

    /**
     * 用于访问服务的客户端
     * 该方式会导致重复实例化restTemplate对象，在高并发下会导致CPU暴涨慎重使用
     * @return RestTemplate
     */
    @Deprecated
    protected RestTemplate getRestTemplate() {
        boolean flag = false;
        try {
            if ("true".equals(sslEnabled)) {
                CloseableHttpClient httpClient = acceptsUntrustedCertsHttpClient();
                if (null != httpClient) {
                    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                            new HttpComponentsClientHttpRequestFactory(httpClient);

                    clientHttpRequestFactory.setReadTimeout(clientReadTimeout);// ms
                    clientHttpRequestFactory.setConnectTimeout(clientConnectTimeout);// ms

                    restTemplate = new RestTemplate(clientHttpRequestFactory);
                    flag = true;
                }
            }
        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException
                | UnrecoverableKeyException | CertificateException | IOException e) {
            logger.error("实例化RestTemplate异常", e);
        }

        if (!flag) {
            SimpleClientHttpRequestFactory clientHttpRequestFactory =
                    new SimpleClientHttpRequestFactory();
            clientHttpRequestFactory.setReadTimeout(clientReadTimeout);// ms
            clientHttpRequestFactory.setConnectTimeout(clientConnectTimeout);// ms
            restTemplate = new RestTemplate(clientHttpRequestFactory);
        }

        return restTemplate;
    }

    /**
     * 获取可复用的访问服务的客户端
     * @return
     */
    protected RestTemplate getReuseRestTemplate() {
        return this.restTemplate;
    }

    /**
     * 调用接口，并封装公共框架
     *
     * @param url
     * @param rpcRequest
     * @return ResponseDto
     */
    public RpcResponse post(String serverAddress,String url, RpcRequest rpcRequest){

        String path = StringUtils.isBlank(url)?"":url;
        if (!StringUtils.isEmpty(getContextPath()) && !"/".equals(getContextPath())) {
            path = getContextPath() + path;
        }
        if ("true".equals(this.sslEnabled)) {
            return postWithContextPath("https://"+serverAddress,rpcRequest, path);
        }else {
            return postWithContextPath("http://"+serverAddress,rpcRequest, path);
        }
    }

    /**
     * @param rpcRequest
     * @param path
     * @return ResponseDto
     */
    protected RpcResponse postWithContextPath(String serverAddress,RpcRequest rpcRequest, String path) {
        RpcResponse response = null;
        try {
            response = call(serverAddress,path, rpcRequest);
        }
        // connect
        catch (ResourceAccessException e) {
            logError(path, rpcRequest, e);
            response = processConnectError(e);
        }
        // 400
        catch (HttpClientErrorException e) {
            logError(path, rpcRequest, e);
            logger.error(e.getMessage(), e);
            response = process4xxError(e);
        }
        // 500
        catch (HttpServerErrorException e) {
            logError(path, rpcRequest, e);
            response = process5xxError(e);
        }
        // others
        catch (Exception e) {
            logError(path, rpcRequest, e);
            response = processDefaultError(e);
        }

        return response;
    }

    /**
     * @param e void
     * @return
     */
    private RpcResponse processDefaultError(Exception e) {
        RpcResponse resDto = new RpcResponse( null, "网络连接失败");;
        String errMsg = e.getMessage();
        if (errMsg != null) {
            resDto = new RpcResponse( HttpStatus.REQUEST_TIMEOUT.name(), errMsg);
        }

        return resDto;
    }

    /**
     * @param e
     * @return
     */
    private RpcResponse processConnectError(ResourceAccessException e) {
        logger.error(e.getMessage(), e);
        return new RpcResponse( HttpStatus.REQUEST_TIMEOUT.name(), "网络连接失败");
    }

    /**
     * @param url
     * @param rpcRequest
     * @param e void
     */
    private void logError(String url, RpcRequest rpcRequest, Exception e) {
        logger.error(String.format("Access Error - url(%s) request (%s)", url, rpcRequest));
        logger.error(e.getMessage(), e);
    }

    /**
     * void
     *
     * @return
     */
    private RpcResponse process5xxError(HttpServerErrorException e) {
        RpcResponse resDto = null;
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(e.getStatusCode())) {
            resDto = getResponseDto(e.getResponseBodyAsString());
        }
        if (resDto == null) {
            resDto = new RpcResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), "系统内部错误");
        }

        return resDto;
    }

    private RpcResponse process4xxError(HttpClientErrorException e) {
        RpcResponse resDto = null;
        HttpStatus statusCode = e.getStatusCode();
        if (HttpStatus.UNAUTHORIZED.equals(statusCode) || HttpStatus.FORBIDDEN.equals(statusCode)
                || HttpStatus.NOT_FOUND.equals(statusCode) || HttpStatus.BAD_REQUEST.equals(statusCode)
                || HttpStatus.REQUEST_TIMEOUT.equals(statusCode)) {
            String code = String.format("framework.network.http.status.%d", statusCode.value());
            resDto = new RpcResponse( statusCode.name(), "00400:请求错误"+code);
        } else if (statusCode != null) {
            resDto = new RpcResponse( statusCode.name(), statusCode.getReasonPhrase());
        } else {
            resDto = new RpcResponse( "系统内部错误", "系统内部错误");
        }

        return resDto;
    }

    /**
     * @param strRes
     * @return ResponseDto
     */
    private RpcResponse getResponseDto(String strRes) {
        RpcResponse resDto = null;
        try {
            resDto = JSON.parseObject(strRes, RpcResponse.class);
        } catch (Throwable e) {
            logger.debug(e.getMessage());
        }
        return resDto;
    }

    /**
     * 封装公共框架的BusinessId、TargetSysId、SourceSysId
     *
     * @param url
     * @param requestDto
     * @return ResponseDto
     */
    private RpcResponse call(String serverAddress,String url, RpcRequest requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Accept", MediaType.ALL_VALUE);

        HttpEntity<RpcRequest> formEntity = new HttpEntity<>(requestDto, headers);
        URI uri = URI.create(serverAddress + url);
        logger.info(String.format("post -> %s", uri.toString()));


        RpcResponse response = null;
        try {
            response = this.getReuseRestTemplate().postForObject(uri, formEntity, RpcResponse.class);
            if (response != null) {
              logger.info("response:{}", JSON.toJSONString(response));
            } else {
              logger.info("response is null!!");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = callGatewayAgain(serverAddress,url, formEntity);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Response: " + JSON.toJSONString(response));
        }

        return response;
    }

    /**
     * @param url
     * @param formEntity
     * @return ResponseDto
     */
    private RpcResponse callGatewayAgain(String serverAddress,String url, HttpEntity<RpcRequest> formEntity){
        URI uri = URI.create(serverAddress + url);
        RpcResponse response= this.getReuseRestTemplate().postForObject(uri, formEntity,RpcResponse.class);
        return response;
    }

    /**
     * 获得系统上下文
     *
     * @return String
     */
    private String getContextPath() {
        return contextPath;
    }

    /**
     * Get pfxpath from resource(include:http,classpath,file)
     *
     * @param pfxpath Cert file path
     * @return InputStream
     * @throws IOException InputStream
     */
    private InputStream getPfxpathInputStream(String pfxpath) throws IOException {
        InputStream instream = null;
        if (StringUtils.startsWith(pfxpath, ResourceUtils.FILE_URL_PREFIX)) {
            instream = new FileInputStream(ResourceUtils.getFile(pfxpath));
        }
        else if (StringUtils.startsWith(pfxpath, "http:")) {
            URL url = new URL(pfxpath);
            instream = url.openStream();
        }
        else {
            instream = this.getClass().getResourceAsStream(pfxpath);
        }
        return instream;
    }

    /**
     *
     * @return CloseableHttpClient
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws CertificateException
     * @throws IOException
     * @throws UnrecoverableKeyException
     */
    private CloseableHttpClient acceptsUntrustedCertsHttpClient()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException,
            CertificateException, IOException, UnrecoverableKeyException {
        logger.debug("Start acceptsUntrustedCertsHttpClient");

        HttpClientBuilder b = HttpClientBuilder.create();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream instream = this.getPfxpathInputStream(pfxpath);
        logger.debug(String.format("client cer path : %s", pfxpath));

        if (null == instream) {
            logger.error("Can't load " + pfxpath);
            return null;
        }
        try {
            keyStore.load(instream, pfxpwd.toCharArray());
            logger.debug(String.format("Load %s sucessfully", pfxpath));
        } finally {
            instream.close();
        }

        logger.debug("Load keystore into sslcontext");
        SSLContext sslcontext =
                SSLContexts.custom().loadKeyMaterial(keyStore, pfxpwd.toCharArray()).build();

        logger.debug("initialize one new sslContext and set it to http client builder");
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        b.setSSLContext(sslContext);

        logger.debug("initialize a ssl socket factory");
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslcontext,
                new String[] {"TLSv1"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        logger.debug("initialize a socket factory registry");
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", sslSocketFactory).build();

        logger.debug("initialize a http client connection manager with the socket factory registry");
        PoolingHttpClientConnectionManager connMgr =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connMgr.setMaxTotal(200);
        connMgr.setDefaultMaxPerRoute(100);
        b.setConnectionManager(connMgr);

        logger.debug("build a ssl http client");
        CloseableHttpClient httpClient = b.build();

        logger.debug("End acceptsUntrustedCertsHttpClient");
        return httpClient;
    }
}
