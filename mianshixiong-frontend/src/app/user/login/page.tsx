"use client";
import {LockOutlined, UserOutlined} from "@ant-design/icons";
import {LoginForm, ProFormText} from "@ant-design/pro-components";
import React, {useState} from "react";
import Image from "next/image";
import Link from "next/link";
import {userLoginUsingPost} from "@/api/userController";
import {message} from "antd";
import {Modal} from "antd";
import {useDispatch} from "react-redux";
import {AppDispatch} from "@/stores";
import {setLoginUser} from "@/stores/loginUser";
import {ProForm} from "@ant-design/pro-form/lib";
import {useRouter} from "next/navigation";
import './index.css';
import {Vertify} from '@alex_xu/react-slider-vertify'; // 导入滑动验证码

/**
 * 用户登录页面
 * @constructor
 */
const UserLoginPage: React.FC = () => {
    const [form] = ProForm.useForm();
    const dispatch = useDispatch<AppDispatch>();
    const router = useRouter();
    const [isVerified, setIsVerified] = useState(false);
    const [visible, setVisible] = useState(false);

    /**
     * 提交
     */
    const doSubmit = async (values: API.UserLoginRequest) => {
        if (!isVerified) {
            setVisible(true); // 打开滑动验证码弹窗
            return;
        }

        try {
            const res = await userLoginUsingPost(values) as any;
            if (res.data) {
                message.success("登录成功");
                // 保存用户登录状态
                dispatch(setLoginUser(res.data));
                router.replace("/");
                form.resetFields();
            }
        } catch (e: any) {
            message.error("登录失败，" + e.message);
            setIsVerified(false); // 重置验证状态
            //刷新页面
            window.location.reload();
        }
    };

    const handleVerify = () => {
        message.success("验证码验证成功");
        setIsVerified(true);
        setVisible(false); // 关闭滑动验证码弹窗
        form.submit(); // 提交表单
    };

    const handleFail = () => {
        message.error("验证码验证失败，请重试");
        setVisible(false); // 关闭滑动验证码弹窗
    };

    const handleRefresh = () => {
        message.info("刷新验证码");
    };

    return (
        <div id="userLoginPage">
            <LoginForm
                form={form}
                logo={
                    <Image src="/assets/logo.png" alt="面试熊" height={44} width={44}/>
                }
                title="面试熊 - 用户登录"
                subTitle="程序员面试刷题网站"
                onFinish={doSubmit}
                submitter={{
                    searchConfig: {
                        submitText: "登录",
                    },
                }}
            >
                <ProFormText
                    name="userAccount"
                    fieldProps={{
                        size: "large",
                        prefix: <UserOutlined/>,
                    }}
                    placeholder={"请输入用户账号"}
                    rules={[
                        {
                            required: true,
                            message: "请输入用户账号!",
                        },
                    ]}
                />
                <ProFormText.Password
                    name="userPassword"
                    fieldProps={{
                        size: "large",
                        prefix: <LockOutlined/>,
                    }}
                    placeholder={"请输入密码"}
                    rules={[
                        {
                            required: true,
                            message: "请输入密码！",
                        },
                    ]}
                />
                <div
                    style={{
                        marginBlockEnd: 24,
                        textAlign: "end",
                    }}
                >
                    还没有账号？
                    <Link href={"/user/register"}>去注册</Link>
                </div>
            </LoginForm>

            {/* 滑动验证码弹窗 */}
            <Modal
                title="滑动验证"
                visible={visible}
                footer={null}
                onCancel={() => setVisible(false)}
            >
                <Vertify
                    width={320}
                    height={160}
                    onSuccess={handleVerify}
                    onFail={handleFail}
                    onRefresh={handleRefresh}
                />
            </Modal>
        </div>
    );
};

export default UserLoginPage;
