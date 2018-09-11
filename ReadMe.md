# AIDL的使用 #

本文从以下来进行介绍AIDL
### 1.	什么是AIDL ###
### 2.	AIDL 支持的数据类型 ###
### 3.	AIDL 如何编写，并且实际操作完成一个简单的AIDL应用 ##
### 4.  实现自定义数据类型的传递
----------



### 1. 什么是AIDL？
为什么会有AIDL呢，因为每一个应用都是在单独的虚拟机中运行，彼此互不影响，如果这两个进程之间需要通信，那么久诞生了AIDL 。
	![](https://i.imgur.com/GQvuszd.png)

图中两个进程之间通信就用到了 `os` 中的一种编程`aidl`(个人理解)

`AIDL`（`Android` 接口定义语言） 是 `Android` 提供的一种进程间通信 (`IPC`) 机制。我们可以利用它定义客户端与服务使用进程间通信 (`IPC`) 进行相互通信时都认可的编程接口。过这种机制，我们只需要写好 `aidl` 接口文件，编译时系统会帮我们生成 `Binder` 接口。而这个`aidl`文件就是规定这两个进程之间通信的数据传递方法和接口的大家都认识的一种通信方式。

### 2. AIDL 支持的数据类型 ###

默认我们创建好的AIDL 文件如下：
	// IMyAidlInterface.aidl
	package com.zx.aidldemo;
	
	// Declare any non-default types here with import statements
	
	interface IMyAidlInterface {
	    /**
	     * Demonstrates some basic types that you can use as parameters
	     * and return values in AIDL.
	     */
	    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
	            double aDouble, String aString);
	}

那么我们修改它的这些基本的数据类型，在参数里添加`byte aBtye ,``char aChar,``short aShort ,`编译后发现系统不认识这`short aShort ,` 所以就不支持这个数据类型了。

####基本数据类型： 

- byte,
- char
- int
- long
- boolean
- float
- double
- String
                    
####2、List 和 Map
####3、String ,CharSequence
####4、Parcelable 的实现实体


###3. AIDL 如何编写

我们通过一个demo来完成，需求：
客户端可以通过传递两个参数，向服务端发起加法运算，服务端将运算完后的结果返回给客户端，在界面展示。

####setp1 服务端
1.	在java目录下创建一个ADIL的目录
![step1](https://i.imgur.com/b2LpP28.png)

2.	创建aidl文件 ![](https://i.imgur.com/d6Fqt94.png)
3.	编写ADIL文件，内容如下,注意其包名：

		package com.zx.aidldemo;
	
		interface IMyAidlInterface {
		    int add(int num1,int num2);
		}
4.	编写服务端的`service`

		package com.zx.aidldemo;
		
		import android.app.Service;
		import android.content.Intent;
		import android.os.IBinder;
		import android.os.RemoteException;
		import android.support.annotation.Nullable;
		
		/**
		 * Created by Administrator on 2018/9/11.
		 */
		
		public class IRemoteService extends Service {
		    @Nullable
		    @Override
		    public IBinder onBind(Intent intent) {
		        return mBinder;
		    }
		
		    private IBinder mBinder = new IMyAidlInterface.Stub(){
		
		        @Override
		        public int add(int num1, int num2) throws RemoteException {
		            return num1+num2;
		        }
		    };
		}
5.  在 `MainActivity` 中，启动该`service`，这样客户端在访问的时候就有了支持。

		package com.zx.aidldemo;
		
		import android.content.Intent;
		import android.support.v7.app.AppCompatActivity;
		import android.os.Bundle;
		
		public class MainActivity extends AppCompatActivity {
		
		    @Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_main);
		        Intent intent = new Intent(this, IRemoteService.class);
		        startService(intent);
		    }
		}
 
6.	在清单文件中配置该服务

		<service
	       android:name=".IRemoteService"
	       android:exported="true"
	       android:process=":remote"/>	

OK，服务端到此为止。

#### SETP2下来我们探索客户端
1.	创建一个`client` 的`module`

	![](https://i.imgur.com/r6h7jMf.png)
	![](https://i.imgur.com/bezLdxM.png)
2.	我们要使用服务端的这个服务，那就必须和服务端有同样的`aidl`定义的接口文件，包名也必须一致，所以我们需要创建一个和服务端相同的目录结构，里面放着相同代码的`*.aidl`文件。so,copy吧。

	![](https://i.imgur.com/hn24RFK.png)
注意，这两个文件必须保持一致（包目录，包名，文件名，文件内容）。
3.	我们开始编写客户端代码

		package com.zx.client;
		
		import android.content.ComponentName;
		import android.content.Context;
		import android.content.Intent;
		import android.content.ServiceConnection;
		import android.os.IBinder;
		import android.os.RemoteException;
		import android.support.v7.app.AppCompatActivity;
		import android.os.Bundle;
		import android.util.Log;
		import android.view.View;
		import android.widget.Button;
		import android.widget.EditText;
		
		import com.zx.aidldemo.IMyAidlInterface;
		
		public class MainActivity extends AppCompatActivity {
		    private EditText ed1,ed2,ed3;
		    private Button btn;
		    private IMyAidlInterface aidl;
		    private ServiceConnection conn = new ServiceConnection() {
		        @Override
		        public void onServiceConnected(ComponentName name, IBinder service) {
		            aidl = IMyAidlInterface.Stub.asInterface(service);
		            Log.d("TAG","onServiceConnected");
		        }
		
		        @Override
		        public void onServiceDisconnected(ComponentName name) {
		            Log.d("TAG","onServiceDisconnected");
		            aidl = null;
		        }
		    };
		    @Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_main);
		
		        autoBindService();
		        initView();
		
		    }
		
		    private void autoBindService() {
		        Intent intent = new Intent();
		        intent.setComponent(new ComponentName("com.zx.aidldemo","com.zx.aidldemo.IRemoteService"));
		        bindService(intent,conn, Context.BIND_AUTO_CREATE);
		    }
		
		    private void initView() {
		        ed1 = (EditText) findViewById(R.id.ed1);
		        ed2 = (EditText) findViewById(R.id.ed2);
		        ed3 = (EditText) findViewById(R.id.ed3);
		        btn = (Button) findViewById(R.id.btn);
		
		        btn.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                int n1 = Integer.parseInt(ed1.getText().toString());
		                int n2 = Integer.parseInt(ed2.getText().toString());
		                int n3=0;
		                try {
		                    n3 = aidl.add(n1,n2);
		                    ed3.setText(n3+"");
		                } catch (RemoteException e) {
		                    e.printStackTrace();
		                    ed3.setText("ERR");
		                }
		            }
		        });
		    }
		}

    `autoBindService()` 用来绑定远程服务端的服务，` intent.setComponent(new ComponentName("com.zx.aidldemo","com.zx.aidldemo.IRemoteService"));` 第一个字符串代表的服务端`AIDL`服务所在的包名，第二个就是要绑定的远程服务的绝对路径。使用`bindService`方式绑定来获取服务度的`binder`对象,进而获取他的`add`方法

	点击按钮的时候去调用`aidl.add(n1,n2);`方法，和服务端进行通信，获取服务端给我们的返回值，显示在界面上。

#### 测试：首先启动服务端，在启动客户端，客户端需要依赖服务端启动的服务的binder对象。
![](https://i.imgur.com/yC40ieJ.png)
![](https://i.imgur.com/ELCqAwi.png)

#### 4 实现自定义数据类型的传递
	
同样的方法我们先构建服务端：

1.	首先构建如下目录和如下的文件

	![](https://i.imgur.com/hr1NDp3.png)

	下面解释一下各个文件
	`IMyAidlInterface.aidl`
	用来两端数据交互的一种通信格式
	
		// IMyAidlInterface.aidl
		package com.zx.aidldemo;
		
		import com.zx.aidldemo.Person;
		interface IMyAidlInterface {
		    /**
		     * 除了基本数据类型，其他类型的参数都需要标明方向类型：in(输入), out(输出), inout(输入输出)
		     */
		    List<Person> add(in Person person);
		}
	当我们给服务端添加一个 `Person` 的数据类型的时候服务端给我们返回当前的`Person`列表.
2.	Person.aidl
	
		// IMyAidlInterface.aidl
		package com.zx.aidldemo;
		
		parcelable Person;  //自定义定义数据类型

	####需要注意的是:
	- 非基本类型的数据需要导入，比如上面的 Person，需要导入它的全路径。
		- 这里的 Person 我理解的是 Person.aidl，然后通过 Person.aidl 又找到真正的实体 Person 类。 
	- 方法参数中，除了基本数据类型，其他类型的参数都需要标上方向类型 
		- in(输入), out(输出), inout(输入输出)

3.	Person.java
	- 实现了`Parcelable`接口，在os中对数据进行序列化传输。
	
			package com.zx.aidldemo;
		
			import android.os.Parcel;
			import android.os.Parcelable;
			
			/**
			 * Created by Administrator on 2018/9/11.
			 */
			
			public class Person implements Parcelable {
			    private String name;
			    private int age;
			
			    public Person(String name, int age) {
			        this.name = name;
			        this.age = age;
			    }
			
			    public String getName() {
			        return name;
			    }
			
			    public void setName(String name) {
			        this.name = name;
			    }
			
			    public int getAge() {
			        return age;
			    }
			
			    public void setAge(int age) {
			        this.age = age;
			    }
			
			    @Override
			    public String toString() {
			        return "Person{" +
			                "name='" + name + '\'' +
			                ", age=" + age +
			                '}';
			    }
			
			
			    /** implements Parcelable 实现序列化  start*/
			    public Person(Parcel source) {
			        this.name = source.readString();// 这里取出数据必须和 writeToParcel 存入数据的先后顺序一致
			        this.age = source.readInt();
			    }
			
			    @Override
			    public int describeContents() {
			        return 0;
			    }
			
			    // 将数据封装成这样的包，在接收的时候仍然需要解包
			    @Override
			    public void writeToParcel(Parcel dest, int flags) {
			        // 将name和数据封装成dest的数据格式 在os中进行传递
			        dest.writeString(name);
			        dest.writeInt(age);
			    }
			
			    /*代码编写格式固定*/
			    public static final Creator<Person> CREATOR = new Creator<Person>() {
			        // 取出writeToParcel 塞进来的数据
			        @Override
			        public Person createFromParcel(Parcel source) {
			            return new Person(source); //通过一个参数的构造
			        }
			
			        @Override
			        public Person[] newArray(int size) {
			            return new Person[size];
			        }
			    };
			    /** implements Parcelable 实现序列化  end*/
			}
	#### 需要注意的是
	- 实现 `Parcelable`接口就需要完成 `CREATOR`对象的创建，其写法固定
	- 在实现单个参数`Person(Parcel source)`的时候 在取数据的时候 一定要按照存入`(writeToParcel希写入)`的先后顺序来取，否则会取数据失败。
		- writeToParcel 将数据写入到序列化中

4.	IRemoteService.java
	
	在客户端添加一次Person 数据对象时候，返回当前添加的列表

		public class IRemoteService extends Service {
		    private ArrayList<Person> persons;
		    @Nullable
		    @Override
		    public IBinder onBind(Intent intent) {
		        return mBinder;
		    }
		
		    private IBinder mBinder = new IMyAidlInterface.Stub(){
		
		        @Override
		        public List<Person> add(Person person) throws RemoteException {
		            persons.add(person);
		            return persons;
		        }
		    };
		}

5.	在MainActivity 中启动服务
	
		public class MainActivity extends AppCompatActivity {
		
		    @Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_main);
		        Intent intent = new Intent(MainActivity.this,IRemoteService.class);
		        startService(intent);
		    }
		}
	
	并且在清单文件中注册服务

	 	<service
            android:name=".IRemoteService"
            android:exported="true"
            android:process=":remote"/>
####服务端告一段落。接下来开发客户端
6. 新建一个Module `client`

	![](https://i.imgur.com/IFzfMe6.png)

	- `aidl` 下的文件同样的和 之前 远程加法的做法一致。新建相同名的包，并且copy文件放进来。
	- 在java目录下新建`com.zx.aidl`的包，放入`Person.java`文件，服务端的文件保持一致。
	
	在MainActivity 方法中加入自己的逻辑。

		import com.zx.aidldemo.IMyAidlInterface;
		import com.zx.aidldemo.Person;
		
		public class MainActivity extends AppCompatActivity {
		    private IMyAidlInterface aidl;
		    private TextView mTv;
		    private ServiceConnection conn = new ServiceConnection() {
		        @Override
		        public void onServiceConnected(ComponentName name, IBinder service) {
		            Log.d("TAG","onServiceConnected");
					// 得到相应的我们定义的AIDL接口的代理对象, 然后可以执行相应的方法
		            aidl = IMyAidlInterface.Stub.asInterface(service);
		        }
		
		        @Override
		        public void onServiceDisconnected(ComponentName name) {
		            Log.d("TAG","onServiceConnected");
		            aidl = null;
		        }
		    };
		
		    @Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_main);
		
		        autuBindService();
		        mTv = (TextView) findViewById(R.id.text);
		        Button btn= (Button) findViewById(R.id.btn);
		        btn.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                try {
		                    List<Person> persons = aidl.add(new Person("zx",27));
		                    Log.d("TAG",persons.toString());
		                    mTv.setText(persons.toString());
		                } catch (RemoteException e) {
		                    e.printStackTrace();
		                }
		            }
		        });
		
		
		    }
		
		    private void autuBindService() {
		        Intent intent = new Intent();
		        //5.0 以后要求显式调用 Service，所以我们无法通过 action 或者 filter 的形式调用 Service
		        intent.setComponent(new ComponentName("com.zx.aidldemo","com.zx.aidldemo.IRemoteService"));
		        bindService(intent,conn, Context.BIND_AUTO_CREATE);
		    }
		
		    @Override
		    protected void onDestroy() {
		        super.onDestroy();
		        unbindService(conn);
		    }
		}	
	- 注意：
		- 在客户端onCreate 的时候，需要进行绑定服务，获取客户端的binder对象，调用add()方法。
		- 5.0 以后要求显式调用 Service，所以我们无法通过 action 或者 filter 的形式调用 Service
		- service 简历连接的方法中 IMyAidlInterface.Stub.asInterface(service) 用来得到相应的我们定义的AIDL接口的代理对象, 然后可以执行相应的方法。
		
