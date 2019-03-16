package com.cc.core.wechat.hook;

import com.cc.core.xposed.BaseXposedHook;

public class TestHooks extends BaseXposedHook {
    @Override
    public void hook(ClassLoader classLoader) {

        /*XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.m", classLoader, int.class, List.class, List.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("=====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.m", classLoader, int.class, List.class, List.class, List.class, String.class, String.class, Map.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("=====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.m", classLoader, String.class, String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("=====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.m", classLoader, String.class, String.class, int.class, byte.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("=====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.plugin.brandservice.b.h", classLoader, String.class, long.class, int.class, int.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("==== brandservice ====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.j", classLoader,
                Context.class, List.class, Intent.class, String.class, int.class, XposedHelpers.findClass("com.tencent.mm.pluginsdk.model.j$a", Wechat.WECHAT_CLASSLOADER),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        for (Object f : param.args) {
                            KLog.e("======>>>>>>  ", f == null ? "null" : f.toString());
                        }
                        KLog.e("======>>>>>>  ", new Exception());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mars.cdn.CdnLogic", classLoader, "startVideoStreamingDownload",
                XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER), int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====+++++==>>>>>>  ", param.args[1] + "  " + StrUtils.toJson(param.args[0]));
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mars.cdn.CdnLogic", classLoader, "startC2CDownload",
                XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++startC2CDownload++==>>>>>>  ", param.args[0] + "  " + StrUtils.toJson(param.args[0]), new Exception());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mars.cdn.CdnLogic", classLoader, "startHttpMultiSocketDownloadTask",
                XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++startHttpMultiSocketDownloadTask++==>>>>>>  ", param.args[0] + "  " + StrUtils.toJson(param.args[0]), new Exception());
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.chatroom.c.g", classLoader, String.class, List.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++Create group++==>>>>>>  ", param.args[0]
                                + "  " + param.args[1], new Exception());
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.plugin.sns.model.ax", classLoader, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++Create group++==>>>>>>  ", param.args[0] + "", new Exception());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.model.ax", classLoader, "commit",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++ax.commit++==>>>>>>  ", StrUtils.toJson(param.thisObject), new Exception());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.storage.s", classLoader, "a",
                XposedHelpers.findClass("com.tencent.mm.plugin.sns.data.h", Wechat.WECHAT_CLASSLOADER),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++MicroMsg.snsMediaStorage++==>>>>>>  ", StrUtils.toJson(param.args[0]), new Exception());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.mmsight.model.CaptureMMProxy", classLoader, "getAccVideoPathInMM",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e("====++CaptureMMProxy.getAccVideoPathInMM++==>>>>>>  ", new Exception());

                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.model.am$a", classLoader, "b",
                XposedHelpers.findClass("com.tencent.mm.plugin.sns.storage.n", Wechat.WECHAT_CLASSLOADER),
                int.class, String.class, XposedHelpers.findClass("com.tencent.mm.protocal.c.brk", Wechat.WECHAT_CLASSLOADER), int.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object f : param.args) {
                            KLog.e("======>>>>>> sns comment ", StrUtils.toJson(f));
                        }
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.av.a", classLoader, List.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++ remark ++==>>>>>>  ", StrUtils.toJson(param.args[0]), new Exception());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.storage.aj", classLoader, "a",
                String.class, XposedHelpers.findClass("com.tencent.mm.storage.ad", Wechat.WECHAT_CLASSLOADER),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object f : param.args) {
                            KLog.e("======>>>>>> ContactStorage ", StrUtils.toJson(f));
                        }
                        KLog.e("======>>>>>> ContactStorage ", new Exception());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.modelvoice.q", classLoader, "P", String.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++ VoiceLogic ++==>>>>>>  ", StrUtils.toJson(param.args[0]) + "  " + param.args[1], new Exception());
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e("====++ VoiceLogic result ++==>>>>>>  ", StrUtils.toJson(param.getResult()) + "  " + (param.getResult() == null ? "false" : new File(param.getResult().toString()).exists()), new Exception());

                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.chatroom.a", classLoader, "a", String.class, List.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++ add group member ++==>>>>>>  ", StrUtils.toJson(param.args[0]) + "  " + param.args[1] + "  " + param.args[2], new Exception());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.chatroom.a", classLoader, "a", String.class, List.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++ add group member ++==>>>>>>  ", StrUtils.toJson(param.args[0]) + "  " + param.args[1] + "  " + param.args[2], new Exception());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.roomsdk.a.b", classLoader, "Xr", String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++ add group member ++==>>>>>>  ", StrUtils.toJson(param.args[0]) + "  " + param.getResult().getClass().getName());
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.vfs.d", classLoader, "adF", String.class,
                new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e("====++ getMd5 ++==>>>>>>  ", StrUtils.toJson(param.getResult()) + "  " + param.args[0]);

                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.ui.ag", classLoader, "a",
                int.class, int.class, findClass("org.c.d.i", Wechat.WECHAT_CLASSLOADER),
                String.class, List.class, findClass("com.tencent.mm.protocal.c.atd", Wechat.WECHAT_CLASSLOADER), int.class,
                boolean.class, List.class, findClass("com.tencent.mm.pointers.PInt", Wechat.WECHAT_CLASSLOADER),
                String.class, int.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====++ ag ++==>>>>>>  ", StrUtils.toJson(o));
                        }

                        KLog.e("======", new Exception());
                    }
                });*/


       /* XposedHelpers.findAndHookConstructor("com.tencent.mm.sdk.platformtools.an", classLoader, Thread.class, Handler.class,
                Runnable.class, Object.class, findClass("com.tencent.mm.sdk.platformtools.an$a", classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++Create group++==>>>>>>  ", StrUtils.toJson(param.args[3])
                                + "  " + param.args[1], new Exception());
                    }
                });*/
        /**
         * l(int paramInt1, int paramInt2)
         *
         *
         *   public l(int paramInt1, int paramInt2, byte paramByte)
         *
         *
         *   public l(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2, com.tencent.mm.ah.g paramG, int paramInt3, String paramString4, String paramString5, boolean paramBoolean, int paramInt4)
         *
         *
         *   public l(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2, final com.tencent.mm.ah.g paramG, int paramInt3, String paramString4, String paramString5, boolean paramBoolean, int paramInt4, int paramInt5, float paramFloat1, float paramFloat2)
         *
         *
         *   public l(long paramLong, String paramString1, String paramString2, String paramString3, int paramInt1, final com.tencent.mm.ah.g paramG, int paramInt2, String paramString4, String paramString5, int paramInt3)
         *
         *   public l(String paramString1, String paramString2, String paramString3, int paramInt)
         *   {
         *     this(paramString1, paramString2, paramString3, paramInt, "", "");
         *   }
         *
         *   public l(String paramString1, String paramString2, String paramString3, int paramInt1, com.tencent.mm.ah.g paramG, int paramInt2, a paramA, int paramInt3)
         *   {
         */

        // int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2, com.tencent.mm.ah.g paramG, int paramInt3, String paramString4, String paramString5, boolean paramBoolean, int paramInt4
        /*XposedHelpers.findAndHookConstructor("com.tencent.mm.as.l", classLoader, String.class, String.class, String.class,
                int.class, XposedHelpers.findClass("com.tencent.mm.ah.g", Wechat.WECHAT_CLASSLOADER), int.class,
                XposedHelpers.findClass("com.tencent.mm.as.l$a", Wechat.WECHAT_CLASSLOADER), int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" : o.toString());
                        }
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.as.l", classLoader, String.class, String.class, String.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" : o.toString());
                        }
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.as.l", classLoader, long.class, String.class, String.class, String.class,
                int.class, XposedHelpers.findClass("com.tencent.mm.ah.g", Wechat.WECHAT_CLASSLOADER), int.class,
                String.class, String.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" :  o.toString());
                        }
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.as.l", classLoader, int.class, String.class, String.class, String.class,
                int.class, XposedHelpers.findClass("com.tencent.mm.ah.g", Wechat.WECHAT_CLASSLOADER), int.class,
                String.class, String.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" :  o.toString());
                        }
                    }
                });*/
        /*XposedHelpers.findAndHookMethod("com.tencent.mm.ap.d", classLoader, "a", long.class, long.class, int.class, Object.class,
                int.class, XposedHelpers.findClass("com.tencent.mm.ap.d$a", Wechat.WECHAT_CLASSLOADER), int.class,

                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" :  o.toString());
                        }
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.ap.k", classLoader, long.class, long.class, int.class,
                XposedHelpers.findClass("com.tencent.mm.af.g", Wechat.WECHAT_CLASSLOADER), int.class,

                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" :  o.toString());
                        }
                    }
                });*/
    }
}
