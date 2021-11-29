**ConvertPic Service** 

**图片转换Jpg/Pdf服务**

# 简介

本服务用于将常见的图片文件转换为Jpg或Pdf文件。支持path、url方式获取源文件；支持对文件加图片水印、文字水印；支持path、ftp、url方式回写文件。

本服务支持的输入文件格式为：BMP、GIF、FlashPix、JPEG、PNG、PMN、TIFF、WBMP。

转换后输出格式为：JPG、PDF。

# 配置说明

本服务的所有配置信息均在于jar包同级文件夹中的application.yml中，默认内容如下：

```yml
# Tomcat
server:
  tomcat:
    uri-encoding: UTF-8
    max-threads: 1000
    min-spare-threads: 30
  # 端口号
  port: 8080
  # 超时时间
  connection-timeout: 5000


# log4j2设置
logging:
  # 配置文件名
  config: log4j2.xml
  level:
    com.thinkdifferent: trace

# 线程设置参数 #######
ThreadPool:
  # 核心线程数10：线程池创建时候初始化的线程数
  CorePoolSize: 10
  # 最大线程数20：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
  MaxPoolSize: 20
  # 缓冲队列200：用来缓冲执行任务的队列
  QueueCapacity: 200
  # 保持活动时间60秒
  KeepAliveSeconds: 60
  # 允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
  AwaitTerminationSeconds: 60


#对于rabbitMQ的支持
spring:
  # RabbitMQ设置
  rabbitmq:
    # 访问地址
    host: 127.0.0.1
    # 端口
    port: 5672
    # 用户名
    username: guest
    # 密码
    password: guest
    # 监听设置
    listener:
      # 生产者
      direct:
        # 自动启动开关
        auto-startup: false
      # 消费者
      simple:
        # 自动启动开关
        auto-startup: false

# 本服务设置
convert:
  pic:
    # 接收的输入文件存储的临时文件夹
    inPutTempPath: D:/cvtest/temp/
    # 默认本地输出文件所在文件夹
    outPutPath: D:/cvtest/
```

可以根据服务器的实际情况进行修改。

重点需要修改的内容：

- 线程参数设置：需要根据实际硬件的承载能力，调整线程池的大小。
- RabbitMQ设置：根据实际软件部署情况，控制是否启用RabbitMQ；如果启用RabbitMQ，一定要根据服务的配置情况修改地址、端口、用户名、密码等信息。
- 本服务设置：根据本服务所在服务器的实际情况，修改本地文件输出路径。

# 使用说明

本服务提供REST接口供外部系统调用，提供了直接转换接口和通过MQ异步转换的接口。

## 图片转换接口说明

直接生成JPG/PDF接口URL：http://host:port/api/convert

MQ异步生成JPG/PDF接口URL：http://host:port/api/convert4mq

接口调用方式：POST

传入参数形式：JSON

传入参数示例：

```JSON
{
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
	"outPutFileName": "001-online",
	"outPutFileType": "jpg",
	"writeBackType": "path",
	"writeBack": {
		"path": "D:/cvtest/"
	}
}
```

以下分块解释传入参数每部分的内容。

### 输入信息

系统支持本地文件路径输入（path）和http协议的url文件下载输入（url）。

当使用文件路径输入时，配置示例如下：

```json
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
```

- inputType：必填，值为“path”。
- inputFile：必填，值为需转换的图片文件（输入文件）在当前服务器中的路径和文件名。

当使用url文件下载输入时，配置示例如下：

```json
	"inputType": "url",
	"inputFile": "http://localhost/file/001.tif",
	"inputHeaders": {
		"Authorization": "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0"
	},
```

- inputType：必填，值为“path”。
- inputFile：必填，值为需转换的图片文件（输入文件）在Web服务中的URL地址。
- inputHeaders：非必填。如果Web服务器访问时需要设置请求头或Token认证，则需要在此处设置请求头的内容；否则此处可不添加。

### 输出信息

可以设置输出的Jpg/Pdf文件的文件名（无扩展名）和输出的文件类型，如下：

```json
	"outPutFileName": "001-online",
	"outPutFileType": "jpg",
```

- outPutFileName：必填，为文件生成后的文件名（无扩展名）。
- outPutFileType：必填，为文件生成后的扩展名。

本例中，即转换后生成名为 001-online.jpg 的文件。

### 回写信息

本服务支持以下回写方式：文件路径（path）、http协议上传（url）、FTP服务上传（ftp）。

注意：返回Base64接口无此部分回写信息。

当使用文件路径（Path）方式回写时，配置如下：

```json
	"writeBackType": "path",
	"writeBack": {
		"path": "D:/data2pdf/"
	},
```

- writeBackType：必填，值为“path”。
- writeBack：必填。JSON对象，path方式中，key为“path”，value为MP4文件回写的路径。

当使用http协议上传（url）方式回写时，配置如下：

```json
	"writeBackType": "url",
	"writeBack": {
		"url": "http://localhost/uploadfile.do"
	},
	"writeBackHeaders": {
		"Authorization": "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0"
	},
```

- writeBackType：必填，值为“url”。
- writeBack：必填。JSON对象，url方式中，key为“url”，value为业务系统提供的文件上传接口API地址。
- writeBackHeaders：非必填。如果Web服务器访问时需要设置请求头或Token认证，则需要在此处设置请求头的内容；否则此处可不添加。

当使用FTP服务上传（ftp）方式回写时，配置如下：

```json
	"writeBackType": "ftp",
	"writeBack": {
		"host": "ftp://localhost",
         "port": "21",
         "username": "guest",
         "password": "guest",
         "basepath": "/jpg/",
         "filepath": "/2021/10/"
	},
```

- writeBackType：必填，值为“ftp”。
- writeBack：必填。JSON对象。
  - host：ftp服务的访问地址。
  - port：ftp服务的访问端口。
  - username：ftp服务的用户名。
  - password：ftp服务的密码。
  - basepath：ftp服务中，此用户的根路径。可用于存放上传时生成的临时文件。
  - filepath：文件所在的下级路径。最终存储的路径为：basepath + filepath 。

### 回调信息

业务系统可以提供一个GET方式的回调接口，在视频文件转换、回写完毕后，本服务可以调用此接口，传回处理的状态。

注意：返回Base64接口无此部分信息。

```json
	"callBackURL": "http://10.11.12.13/callback.do"
```

回调接口需要接收两个参数：

- file：处理后的文件名。本例为“001-online”。
- flag：处理后的状态，值为：success 或 error。

接口url示例：

```http
http://10.11.12.13/callback.do?file=001-online&flag=success
```

### 返回信息

接口返回信息示例如下：

```json
{
  "flag": "success",
  "message": "Convert Pic to JPG/PDF success."
}
```

- flag：处理状态。success，成功；error，错误，失败。
- message：返回接口消息。



## 生成PDF后转换为Base64字符串返回

生成一个PDF后返回Base64字符串接口URL：http://host:port/api/convert2base64

生成多个PDF后返回Base64字符串接口URL：http://host:port/api/convert2base64s

接口调用方式：POST

传入参数形式：JSON

传入参数示例：

```JSON
{
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
	"outPutFileName": "001-online",
	"outPutFileType": "jpg"
}
```

以下分块解释传入参数每部分的内容。

### 输入信息

系统支持本地文件路径输入（path）和http协议的url文件下载输入（url）。

配置示例请见上述章节。

### 输出信息

可以设置输出的Jpg/Pdf文件的文件名（无扩展名）和输出的文件类型，如下：

```json
	"outPutFileName": "001-online",
	"outPutFileType": "jpg",
```

- outPutFileName：必填，为文件生成后的文件名（无扩展名）。
- outPutFileType：必填，为文件生成后的扩展名。本接口中，可以设置：jpg、pdf、jpg,pdf。

### 返回信息

convert2base64接口返回信息示例如下：

```json
JVBERi0xLjQKJeLjz9MKNCAwIG9iago8PC9TdWJ0eXBlL0Zvcm0vRmlsdGVyL0ZsYXRlRGVjb2RlL1R5………………
```

- 返回Base64编码后的Jpg/Pdf文件的内容。可供前端页面直接将其放入iframe的src属性中显示。

convert2base64s接口返回信息示例如下：

```json
{
    "flag": "success",
    "message": "Convert Pic to JPG/PDF success",
    "base64": [
        {
            "filename": "001-online.jpg",
            "base64": "JVBERi0xLjQKJeLjz9MKNCAwIG9iago8PC9TdWJ0eXBlL0Zvcm0vRmlsdGVy…………"
        },
        {
            "filename": "001-online.pdf",
            "base64": "JVBERi0xLjQKJeLjz9MKNCAwIG9iago8PC9TdWJ0eXBlL0Zvcm0vRmlsdGVy…………"
        }
    ]
}
```

- flag：处理状态。success，成功；error，错误，失败。
- message：返回接口消息。
- base64
  - filename：文件名
  - base64：文件Base64编码之后的字符串。

# 代码结构说明

本项目所有代码均在  com.thinkdifferent.convertpic 之下，包含如下内容：

- config
  - ConvertPicConfig：本服务自有配置读取。
  - RabbitMQConfig：RabbitMQ服务配置读取。
- consumer
  - ConvertPicConsumer：MQ消费者，消费队列中传入的JSON参数，执行任务（Task）。
- controller
  - ConvertPic：REST接口，提供直接转换Jpg/Pdf接口，和调用MQ异步生成PDF的接口。
- service
  - ConvertPicService：图片转换为Jpg/Pdf文件、文件回写上传、接口回调等核心逻辑处理。
  - RabbitMQService：将JSON消息加入到队列中的服务层处理。
- task
  - Task：异步多线程任务，供MQ消费者调用，最大限度的提升并行能力。
- utils
  - ConvertPicUtil：图片转换处理的工具类，可以将传入的图片转换为Jpg或Pdf。
  - FtpUtil：FTP访问工具，包括上传、下载、删除等。
  - GetFileUtil：HttpGet方式获取文件工具类。
  - WaterMarkUtil：水印处理工具类，支持图片水印、文字水印。
  - WriteBackUtil：回写文件、回调接口的工具类。