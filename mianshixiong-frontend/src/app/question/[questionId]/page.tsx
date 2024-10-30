// "use server";
// import {getQuestionVoByIdUsingGet} from "@/api/questionController";
// import QuestionCard from "@/components/QuestionCard";
// import "./index.css";
//
// /**
//  * 题目详情页
//  * @constructor
//  */
// export default async function QuestionPage({params}:any) {
//     const {questionId} = params;
//
//     // 获取题目详情
//     let question = undefined as any;
//     try {
//         const res = await getQuestionVoByIdUsingGet({
//             id: questionId,
//         });
//         question = res.data;
//     } catch (e:any) {
//         console.error("获取题目详情失败，" + e.message);
//     }
//     // 错误处理
//     if (!question) {
//         return <div>获取题目详情失败，请刷新重试</div>;
//     }
//
//     return (
//         <div id="questionPage">
//             <QuestionCard question={question}/>
//         </div>
//     );
// }

"use client";
import {message} from "antd";
import {getQuestionVoByIdUsingGet} from "@/api/questionController";
import "./index.css";
import {useEffect, useState} from "react";
import QuestionCard from "@/components/QuestionCard";

/**
 * 题目详情页
 * @constructor
 */
export default function QuestionPage({params}: any) {
    const {questionId} = params;
    //用于存储获取到的题目详情。
    const [question, setQuestion]: any = useState(null);
    //用于表示数据是否正在加载中。
    // const [loading, setLoading] = useState(true);
    //用于存储可能发生的错误信息。
    // const [error, setError]: any = useState(null);

    useEffect(() => {
        getQuestionVoByIdUsingGet({id: questionId})
            .then((res: any) => {
                setQuestion(res.data);
            })
            .catch((e) => {
                message.error("获取题目详情失败，" + e.message);
            });
    }, [questionId]); // 注意这里的依赖数组，确保只在 questionId 变化时重新请求


    return (
        <div id="questionPage">
            <QuestionCard question={question}/>
        </div>
    );
}


