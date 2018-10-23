package com.example.zjf.rxjavabasiclearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 *@description Rxjava的使用方式有两种：
                1、分步骤实现：该方法主要为了深入说明Rxjava的原理 & 使用，主要用于演示说明
                    1）创建被观察者 Observable & 生产事件---------------顾客入饭店 - 坐下餐桌 - 点菜
                    2）创建观察者 Observer 并 定义响应事件行为----------开厨房 - 确定对应菜式
                    3）通过订阅（subscribe）连接观察者和被观察者--------顾客找到服务员 - 点菜 - 服务员下单到厨房 - 厨房烹调
                2、 基于事件流的链式调用：主要用于实际使用
                    整体方法调用顺序：观察者.onSubscribe（）> 被观察者.subscribe（）> 观察者.onNext（）>观察者.onComplete()
 *
 *@author zjf
 *@date 2018/10/22 20:19
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Disposable mDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"onCreate");
        // 步骤1：创建被观察者 Observable & 生产事件---即 顾客入饭店 - 坐下餐桌 - 点菜
        //  1. 创建被观察者 Observable 对象
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            // 2. 在复写的subscribe（）里定义需要发送的事件
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                // 通过 ObservableEmitter类对象产生事件并通知观察者
                // ObservableEmitter类介绍
                // a. 定义：事件发射器
                // b. 作用：定义需要发送的事件 & 向观察者发送事件
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });

        // 步骤2：创建观察者 Observer 并 定义响应事件行为
        // 即 开厨房 - 确定对应菜式
        Observer<Integer> observer = new Observer<Integer>() {
            // 通过复写对应方法来 响应 被观察者
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件"+ value +"作出响应"  );
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };
        // 步骤3：通过订阅（subscribe）连接观察者和被观察者
        // 即 顾客找到服务员 - 点菜 - 服务员下单到厨房 - 厨房烹调
        observable.subscribe(observer);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        // RxJava的流式操作
        // 1. 定义Disposable类变量
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件"+ value +"作出响应"  );
                if (value == 2) {
                    // 设置在接收到第二个事件后切断观察者和被观察者的连接
                    mDisposable.dispose();
                    Log.d(TAG, "已经切断了连接：" + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        });
    }
}
