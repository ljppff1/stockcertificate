package com.tust.tools.service;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;

public class DongHuaYanChi {
	    //开始和结束  参数：视图，上下文，handler,开始动画，结束动画，延迟时间
	    public static void dongHuaSandE(final View v,final Context context,final Handler handler,final int sanimId,final int eanimId,final int time) {
	        new Thread() {
	            public void run() {
	                try {
	                    handler.post(new Runnable(){
	                    	public void run(){
	                    		v.startAnimation(AnimationUtils.loadAnimation(context, sanimId));
	                    	}
	                    });
	                    sleep(time);
	                    handler.post(new Runnable(){
	                    	public void run(){
	                    		v.startAnimation(AnimationUtils.loadAnimation(context, eanimId));
	                    	}
	                    });
	                    sleep(time);
	                    v.setVisibility(View.GONE);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }.start();
	    }
	    
	       public static void dongHuaSandE(final View sv,final View ev,final Context context,final Handler handler,final int sanimId,final int eanimId,final int time) {
	            new Thread() {
	                public void run() {
	                    try {
	                        handler.post(new Runnable(){
	                            public void run(){
	                                sv.startAnimation(AnimationUtils.loadAnimation(context, sanimId));
	                            }
	                        });
	                        sleep(time);
	                        handler.post(new Runnable(){
	                            public void run(){
	                                sv.setVisibility(View.GONE);
	                                ev.setVisibility(View.VISIBLE);
	                                ev.startAnimation(AnimationUtils.loadAnimation(context, eanimId));
	                            }
	                        });
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            }.start();
	        }
	    //结束动画 参数： 视图，上下文，handler,开始动画，延迟时间
	    public static void dongHuaEnd(final View v,final Context context,final Handler handler,final int eanimId,final int time) {
	        new Thread() {
	            public void run() {
	                try {
	                    handler.post(new Runnable(){
	                    	public void run(){
	                    		v.startAnimation(AnimationUtils.loadAnimation(context, eanimId));
	                    	}
	                    });
	                    sleep(time);
	                    handler.post(new Runnable(){
	                    	public void run(){
	                    		v.setVisibility(View.GONE);
	                    	}
	                    });
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }.start();
	    }
	    
	  //结束动画 参数： 视图，上下文，handler,开始动画，延迟时间
	    public static void dongHuaDialogEnd(final Dialog d,final View v,final Context context,final Handler handler,final int eanimId,final int time) {
	        new Thread() {
	            public void run() {
	                try {
	                    handler.post(new Runnable(){
	                    	public void run(){
	                    		v.startAnimation(AnimationUtils.loadAnimation(context, eanimId));
	                    	}
	                    });
	                    sleep(time);
	                    handler.post(new Runnable(){
	                    	public void run(){
	                    		d.cancel();
	                    		d.dismiss();
	                    	}
	                    });
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }.start();
	    }
	    
	  //结束动画 参数： 视图，上下文，handler,开始动画，延迟时间
	    public static void dongHuaStart(final View v,final Context context,final Handler handler,final int eanimId,final int time) {
	        new Thread() {
	            public void run() {
	                try {
	                	sleep(time);
	                    handler.post(new Runnable(){
	                    	public void run(){
	                    		v.setVisibility(View.VISIBLE);
	                    		v.startAnimation(AnimationUtils.loadAnimation(context, eanimId));
	                    	}
	                    });
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }.start();
	    }
}
