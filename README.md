## 1.Import and use SDK
### 1.1	导入module工程mokosupport
### 1.2	配置settings.gradle文件，引用mokosupport工程：

	include ':app',':mokosupport'

### 1.3	编辑主工程的build.gradle文件：

	dependencies {
	    compile fileTree(dir: 'libs', include: ['*.jar'])
	    compile project(path: ': mokosupport')
	}

### 1.4	在工程初始化时导入sdk：

	public class BaseApplication extends Application {
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        // 初始化
	        MokoSupport.getInstance().init(getApplicationContext());
	    }
	}


## 2.Function Introduction

- sdk中提供的方法包括：与WIFI设备的Socket通信，MQTT连接服务，断开连接，订阅主题，取消订阅主题，发布主题，日志记录等；
- Socket通信通过`SocketService`调用；
- MQTT通信可通过`MokoSupport.getInstance()`调用；

### 2.1 SocketService

创建Socket连接前需要确认APP是否已连接上设备的WIFI，连接默认IP地址`192.168.4.1`，默认端口号`8266`，可在`SocketThread`中修改

#### 2.1.1 初始化

	bindService(new Intent(this, SocketService.class), mServiceConnection, BIND_AUTO_CREATE);

启动SocketService，并获取SocketService对象，调用`mService.startSocket()`创建Socket线程，连接设备，连接成功后线程等待发送信息；

#### 2.1.2 获取连接状态和应答

1、通过注册广播获取连接状态：

广播ACTION：`MokoConstants.ACTION_AP_CONNECTION`

连接状态：

- 连接成功：`MokoConstants.CONN_STATUS_SUCCESS`
- 连接中：`MokoConstants.CONN_STATUS_CONNECTING`
- 连接失败：`MokoConstants.CONN_STATUS_FAILED`
- 连接超时：`MokoConstants.CONN_STATUS_TIMEOUT`

2、通过注册广播获取Socket通信应答：

广播ACTION：`MokoConstants.ACTION_AP_SET_DATA_RESPONSE`

获取应答：

	DeviceResponse response = (DeviceResponse) intent.getSerializableExtra(MokoConstants.EXTRA_AP_SET_DATA_RESPONSE);

#### 2.1.3 Socket通信

发送数据只接受JSON格式的字符串

eg:

1、获取设备信息：

	{ 
	          "header" : 4001
	 }
	 
response：

	 { 
	     "code" : 0, 
	     "message" : "success", 
	     "result" : { 
	          "header" : 4001, 
	          "device_function" : "iot_plug", 
	          "device_name" : "plug_one", 
	          "device_specifications" : "us", 
	          "device_mac" : "11:22:33:44:55:66",
	          "device_type" : "1"
	     } 
	 }
	 
2、	发送MQTT服务器信息

	{ 
	          "header" : 4002, 
	          "host" : "45.32.33.42", 
	          "port" : 1883, 
	          "connect_mode" : 0, 
	          "username" : "DVES_USER", 
	          "password" : "DVES_PASS", 
	          "keepalive" : 120, 
	          "qos" : 2, 
	          "clean_session" :1
	 }
	 
response：

	{ 
	     "code" : 0, 
	     "message" : "success", 
	     "result" : { 
	         "header" : 4002
	     } 
	 }
	 
3、发送特定SSID的WIFI网络

	{ 
	          "header" : 4003, 
	          "wifi_ssid" : "Fitpolo", 
	          "wifi_pwd" : "fitpolo1234.", 
	          "wifi_security" : 3 
	 }
	 
response:

	{ 
	     "code" : 0, 
	     "message" : "success", 
	     "result" : { 
	       "header" : 4003
	     } 
	 }



### 2.2	MokoSupport

#### 2.2.1 连接MQTT服务器

1、创建`MqttAndroidClient`

	public void creatClient(String host, String port, String clientId, boolean tlsConnection)
	
2、连接服务器

	public void connectMqtt(MqttConnectOptions options)
	
根据`MqttCallbackHandler`获取创建状态，接收服务器返回数据

	@Override
    public void connectComplete(boolean reconnect, String serverURI) {
        ...
    }
    @Override
    public void connectionLost(Throwable cause) {
        ...
    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        ...
    }
    
3、通过注册广播获取连接状态：

广播ACTION：`MokoConstants.ACTION_MQTT_CONNECTION`

连接状态：

- 连接成功：`MokoConstants.MQTT_CONN_STATUS_SUCCESS`
- 连接断开：`MokoConstants.MQTT_CONN_STATUS_LOST`

4、通过注册广播接收服务器返回数据

广播ACTION：`MokoConstants.ACTION_MQTT_RECEIVE`

返回数据：

- 返回数据Topic：`MokoConstants.EXTRA_MQTT_RECEIVE_TOPIC`
- 返回数据Message：`MokoConstants.EXTRA_MQTT_RECEIVE_MESSAGE`

返回数据是JSON格式，eg：

	{ 
	          "company_name" : "moko", 
	          "production_date" : "201801", 
	          "product_model" : "plug_one", 
	          "firmware_version" : "000001" 
	          "device_mac" : "11:22:33:44:55:66"
	 }

#### 2.2.2 Action监听

MQTT通信包含四种Action，执行每种Action都需要设置`ActionListener`，以此来监听Action的状态：

	public enum Action {
	        /**
	         * Connect Action
	         **/
	        CONNECT,
	        /**
	         * Subscribe Action
	         **/
	        SUBSCRIBE,
	        /**
	         * Publish Action
	         **/
	        PUBLISH,
	        /**
	         * UnSubscribe Action
	         **/
	        UNSUBSCRIBE
	    }
	    
通过注册广播获取Action状态：

1、CONNECT
	
广播ACTION：`MokoConstants.ACTION_MQTT_CONNECTION`

- 连接失败：`MokoConstants.MQTT_CONN_STATUS_FAILED`

2、SUBSCRIBE

广播ACTION：`MokoConstants.ACTION_MQTT_SUBSCRIBE`

- 订阅Topic：`MokoConstants.EXTRA_MQTT_RECEIVE_TOPIC`
- 订阅状态：`MokoConstants.EXTRA_MQTT_STATE`

3、PUBLISH

广播ACTION：`MokoConstants.ACTION_MQTT_PUBLISH`

- 发布状态：`MokoConstants.EXTRA_MQTT_STATE`

4、UNSUBSCRIBE

广播ACTION：`MokoConstants.ACTION_MQTT_UNSUBSCRIBE`

- 取消订阅状态：`MokoConstants.EXTRA_MQTT_STATE`

#### 2.2.3 订阅主题

	MokoSupport.getInstance().subscribe(String topic, int qos)
	
#### 2.2.4 发布信息

	MokoSupport.getInstance().publish(String topic, MqttMessage message)

#### 2.2.5 取消订阅主题

	MokoSupport.getInstance().unSubscribe(String topic)
	
#### 2.2.6 判断MQTT是否连接

	MokoSupport.getInstance().isConnected()
	

	

## 3.Save Log to SD Card

- SDK中集成了Log保存到SD卡的功能，引用的是[https://github.com/elvishew/xLog](https://github.com/elvishew/xLog "XLog")
- 初始化方法在`MokoSupport.getInstance().init(getApplicationContext())`中实现
- 可修改在SD卡上保存的文件名夹名和文件名

		public class LogModule {
			private static final String TAG = "mokoLife";// 文件名
		    private static final String LOG_FOLDER = "mokoLife";// 文件夹名
			...
		}

- 存储策略：仅保存当天数据和前一天数据，前一天数据以.bak为后缀
- 调用方式：
	- LogModule.v("log info");
	- LogModule.d("log info");
	- LogModule.i("log info");
	- LogModule.w("log info");
	- LogModule.e("log info");


