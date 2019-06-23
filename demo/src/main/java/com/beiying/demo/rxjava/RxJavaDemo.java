package com.beiying.demo.rxjava;



import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Created by beiying on 18/8/16.
 */

public class RxJavaDemo {
    public static final String TAG = "RxJava";

    public static void main(String[] args) throws Exception{
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(512, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String publicKeyStr = getPublicKeyStr(publicKey);
        String privateKeyStr = getPrivateKeyStr(privateKey);

        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(publicKeyStr.getBytes());

        System.out.println("公钥\r\n" + toHexString(algorithm.digest(), ""));

        algorithm.reset();
        algorithm.update(privateKeyStr.getBytes());
        System.out.println("私钥\r\n" + toHexString(algorithm.digest(), ""));
    }

    public static String toHexString(byte[] bytes, String separtor) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex).append(separtor);
        }
        return hexString.toString();
    }


    public static String getPrivateKeyStr(PrivateKey privateKey)
            throws Exception {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
//        return Base64.encodeToString(privateKey.getEncoded(), .NO_WRAP);
    }

    public static String getPublicKeyStr(PublicKey publicKey) throws Exception {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

//    public static void main(String[] args) {
//
//
//
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                Log.e(TAG, "Observable emit 1" + "\n");
//                emitter.onNext(1);
//                Log.e(TAG, "Observable emit 2" + "\n");
//                emitter.onNext(2);
//                Log.e(TAG, "Observable emit 3" + "\n");
//                emitter.onNext(3);
//                emitter.onComplete();
//                Log.e(TAG, "Observable emit 4" + "\n");
//                emitter.onNext(4);
//            }}).subscribeOn(Schedulers.newThread())
//                .observeOn(Schedulers.io())
//                .subscribe(new Observer<Integer>() {
//                    private int i;
//                    private Disposable mDisposable;
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e(TAG, "onSubscribe" + "\n");
//                        mDisposable = d;
//                    }
//
//                    @Override
//                    public void onNext(Integer integer) {
//                        i++;
//                        Log.e(TAG, "onNext data=" + integer + ";i=" + i + "\n");
//                        if (i == 2) {
//                            mDisposable.dispose();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError : value : " + e.getMessage() + "\n");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e(TAG, "onComplete" + "\n");
//                    }
//                });
//
//        Flowable.just("hello beiying").subscribe(System.out::println);
//
//    }

    static class Log {
        public static void e(String tag, String text) {
            System.out.println(tag + ":" + text);
        }
    }

}
