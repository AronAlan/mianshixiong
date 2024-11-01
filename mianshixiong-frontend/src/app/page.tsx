import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";
import { getRecommendQuestionVoByIdUsingGet, listQuestionVoByPageUsingPost } from "@/api/questionController";
// import { listRecommendQuestionVoUsingGet } from "@/api/questionController";
import QuestionBankList from "@/components/QuestionBankList";
import QuestionList from "@/components/QuestionList";
import { Card, Divider, Flex } from "antd";
import Title from "antd/es/typography/Title";
import Link from "next/link";
import "./index.css";
import RecommendQuestionList from "@/components/RecommendQuestionList";

// 本页面使用服务端渲染，禁用静态生成
export const dynamic = "force-dynamic";

/**
 * 主页
 * @constructor
 */
export default async function HomePage() {
    let questionBankList = [];
    let questionList = [];
    let recommendList = [];

    try {
        const questionBankRes = (await listQuestionBankVoByPageUsingPost({
            pageSize: 12,
            sortField: "createTime",
            sortOrder: "descend"
        })) as any;
        questionBankList = questionBankRes.data.records ?? [];
    } catch (e: any) {
        console.error("获取题库列表失败，" + e.message);
    }

    try {
        const questionListRes = (await listQuestionVoByPageUsingPost({
            pageSize: 12,
            sortField: "createTime",
            sortOrder: "descend"
        })) as any;
        questionList = questionListRes.data.records ?? [];
    } catch (e: any) {
        console.error("获取题目列表失败，" + e.message);
    }

    try {
        const recommendRes = (await getRecommendQuestionVoByIdUsingGet()) as any;
        recommendList = recommendRes.data ?? [];
    } catch (e: any) {
        console.error("获取推荐列表失败，" + e.message);
    }

    return (
        <div id="homePage" className="max-width-content">
            <Card
                style={{
                    borderRadius: 8
                }}
            >
                <div>
                    <div
                        style={{
                            fontSize: "30px",
                            fontWeight: "bold",
                            textAlign: "center"
                        }}
                    >
                        ❤️欢迎使用 面试熊刷题平台❤️
                    </div>
                    <p
                        style={{
                            fontSize: "20px",
                            color: "black",
                            lineHeight: "40px",
                            marginTop: 40,
                            textAlign: "center"
                        }}
                    >
                        🚀 面试熊刷题平台
                        提供丰富的面试题目，帮您快速找到最合适的题目，帮您快速了解题目知识点
                        🚀
                    </p>
                    <p
                        style={{
                            fontSize: "20px",
                            color: "black",
                            lineHeight: "40px",
                            textAlign: "center"
                        }}
                    >
                        🌐 致力于为用户提供热门面试题 🌐
                    </p>
                    <p
                        style={{
                            fontSize: "30px",
                            color: "black",
                            lineHeight: "40px",
                            marginTop: 30,
                            textAlign: "center",
                            fontFamily: "ZoomlaWenzhengming-A064"
                        }}
                    >
                        雄关漫道真如铁，而今迈步从头越
                    </p>
                    <p
                        style={{
                            fontSize: "18px",
                            lineHeight: "40px",
                            marginTop: 30,
                            textAlign: "center"
                        }}
                    >
                        在法律允许的范围内，在此声明，本平台仅用于交流学习，不用于商业用途。不承担用户或任何人士因本网站所提供的信息或任何链接所引致的任何直接、间接、附带、从属、特殊、惩罚性或惩戒性的损害赔偿（包括但不限于收益、预期利润的损失或失去的业务）。
                    </p>
                    <p
                        style={{
                            fontSize: "18px",
                            lineHeight: "40px",
                            textAlign: "center"
                        }}
                    >
                        本网站图片，题目和答案，皆源于互联网，由作者整理，如果侵犯，请及时通知我们，本网站将在第一时间及时删除。
                    </p>
                </div>
            </Card>

            <Divider />

            <Flex justify="space-between" align="center">
                <Title level={3}>最新题库</Title>
            </Flex>
            <QuestionBankList questionBankList={questionBankList} />
            <div style={{ textAlign: 'center', margin: '5px 0' }}>
                <Link href="/banks">
                    <button className="view-more-btn">
                        查看更多题库
                    </button>
                </Link>
            </div>

            <Divider />

            {/* 修改这部分，添加 Flex 容器包裹最新题目和推荐栏 */}
            <Title level={3}>最新题目</Title>
            
            {/* 修改这部分，将 Flex 容器移到标题下方 */}
            <Flex justify="space-between" align="flex-start" gap={20}>
                {/* 左侧最新题目列表 */}
                <div style={{ flex: 2 }}>
                    <QuestionList questionList={questionList} />
                    <div style={{ textAlign: 'center', margin: '5px 0' }}>
                        <Link href="/questions">
                            <button className="view-more-btn">
                                查看更多题目
                            </button>
                        </Link>
                    </div>
                </div>

                {/* 右侧推荐栏 */}
                <div style={{ flex: 1 }}>
                    <Card
                        title={<Title level={4} style={{ marginBottom: 0 }}>为您推荐</Title>}
                        style={{ 
                            borderRadius: 8,
                            backgroundColor: '#eff6e6'
                        }}
                        headStyle={{
                            backgroundColor: '#dfeecd',
                            padding: '8px 16px'  // 减小标题区域的内边距
                        }}
                        bodyStyle={{
                            paddingTop: '8px',  // 减小内容区域的上内边距
                            paddingBottom: '8px'  // 减小底部内边距
                        }}
                    >
                        <RecommendQuestionList questionList={recommendList} />
                    </Card>
                </div>
            </Flex>
        </div>
    );
}
