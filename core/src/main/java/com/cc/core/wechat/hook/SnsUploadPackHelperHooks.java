package com.cc.core.wechat.hook;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class SnsUploadPackHelperHooks extends BaseXposedHook {
    private static final String n = "com.tencent.mm.plugin.sns.model.ax";
    @Override
    public void hook(@NotNull ClassLoader classLoader) {

        //region 6.7.2
        if ("6.7.2".equals(Wechat.WechatVersion)) {
            hookMethod(n, classLoader, "MI", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#MI <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "MJ", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#MJ <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "MK", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#MK <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "ML", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#ML <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "MM", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#MM <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "MN", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#MN <<<<<",StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "MO", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#MO <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "MP", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#MP <<<<<",StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "MQ", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#MQ <<<<<",StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "MR", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#MR <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "X", String.class, String.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e(">>>> UploadPackHelper#X <<<<<", param.args[0]
                            + " : "
                            + param.args[1]
                            + " : "
                            + param.args[2]);
                    }
                });
            hookMethod(n, classLoader, "a", findClass("com.tencent.mm.protocal.c.atd", classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e(">>>> UploadPackHelper#a <<<<<", StrUtils.toJson(param.args[0]));
                    }
                });
            hookMethod(n, classLoader, "ah", LinkedList.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#ah <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "ct", List.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#ct <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "cu", List.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#cu <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "eE", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#eE <<<<<",
                        StrUtils.toJson(param.args[0]) + " : " + StrUtils.toJson(param.args[1]));
                }
            });
            hookMethod(n, classLoader, "eG", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#eG <<<<<",
                        StrUtils.toJson(param.args[0]) + " : " + StrUtils.toJson(param.args[1]));
                }
            });
            hookMethod(n, classLoader, "f", String.class, String.class, String.class, int.class,
                int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e(">>>> UploadPackHelper#f <<<<<",
                            param.args[0]
                                + " : "
                                + param.args[1]
                                + " : "
                                + param.args[2]
                                + " : "
                                + param.args[3]
                                + " : "
                                + param.args[4]);
                    }
                });
            hookMethod(n, classLoader, "q", String.class, String.class, String.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e(">>>> UploadPackHelper#q <<<<<",
                            param.args[0] + " : " + param.args[1]
                                + " : " + param.args[2] + " : " + param.args[3], new Exception());
                    }
                });
            hookMethod(n, classLoader, "setSessionId", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#setSessionId <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "xf", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#xf <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "xg", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#xg <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "xh", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#xh <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "xi", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#xi <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "xj", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#xj <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "xk", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#xk <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "b", byte[].class, String .class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#b <<<<<", param.args[1].toString() + "   " + param.args[2].toString());
                }
            });

        }
        //endregion 6.7.2

        //region 7.0.3
        else if ("7.0.3".equals(Wechat.WechatVersion)) {

            hookMethod(n, classLoader, "WU", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#WU <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "WV", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#WV <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "WW", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#WW <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "WX", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#WX <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "WY", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#WY <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "WZ", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#WZ <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "Xa", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Xa <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "Xb", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Xb <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "Xc", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Xc <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "Xd", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Xd <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "ad", String.class, String.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e(">>>> UploadPackHelper#ad <<<<<", StrUtils.toJson(param.args[0])
                            + " : "
                            + StrUtils.toJson(param.args[1])
                            + " : "
                            + StrUtils.toJson(param.args[2]));
                    }
                });
            hookMethod(n, classLoader, "a", findClass("com.tencent.mm.protocal.protobuf.axc", classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e(">>>> UploadPackHelper#a <<<<<", StrUtils.toJson(param.args[0]));
                    }
                });
            hookMethod(n, classLoader, "ar", LinkedList.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#ar <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "de", List.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#de <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "df", List.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#df <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "fT", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#fT <<<<<",
                        StrUtils.toJson(param.args[0]) + " : " + StrUtils.toJson(param.args[1]));
                }
            });
            hookMethod(n, classLoader, "fV", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#fV <<<<<",
                        StrUtils.toJson(param.args[0]) + " : " + StrUtils.toJson(param.args[1]));
                }
            });
            hookMethod(n, classLoader, "f", String.class, String.class, String.class, int.class,
                int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e(">>>> UploadPackHelper#f <<<<<",
                            param.args[0]
                                + " : "
                                + param.args[1]
                                + " : "
                                + param.args[2]
                                + " : "
                                + param.args[3]
                                + " : "
                                + param.args[4]);
                    }
                });
            hookMethod(n, classLoader, "s", String.class, String.class, String.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.e(">>>> UploadPackHelper#s <<<<<",
                            param.args[0] + " : " + param.args[1]
                                + " : " + param.args[2] + " : " + param.args[3]);
                    }
                });
            hookMethod(n, classLoader, "setSessionId", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#setSessionId <<<<<", StrUtils.toJson(param.args[0]));
                }
            });
            hookMethod(n, classLoader, "Cm", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Cm <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "Cn", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Cn <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "Co", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Co <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "Cp", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Cp <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "Cq", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Cq <<<<<", param.args[0].toString());
                }
            });
            hookMethod(n, classLoader, "Cr", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e(">>>> UploadPackHelper#Cr <<<<<", param.args[0].toString());
                }
            });
        }
        //endregion 7.0.3
    }
}
