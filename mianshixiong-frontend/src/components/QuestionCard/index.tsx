"use client";
import MdViewer from "@/components/MdViewer";
import TagList from "@/components/TagList";
import { Button, Card, Space, Spin } from "antd";
import { LikeOutlined, StarOutlined } from "@ant-design/icons";
import Title from "antd/es/typography/Title";
import "./index.css";
import { useCallback, useMemo, useState } from "react";
import { addQuestionFavourCountUsingPost, addQuestionLikeCountUsingPost, deleteQuestionLikeCountUsingDelete, deleteQuestionFavourCountUsingDelete } from "@/api/questionController";
import { message } from "antd/lib";

interface Props {
    question?: API.QuestionVO;
}

/**
 * 题目卡片
 * @param props
 * @constructor
 */
const QuestionCard = (props: Props) => {
    const { question } = props;
    if (!question) {
        return <Spin tip="加载中..." />;
    }

    // 点赞状态
    const [isLiked, setIsLiked] = useState(question?.isLike === 1);
    const [likeCount, setLikeCount] = useState(question?.likeCount || 0);
    // 收藏状态
    const [isFavoured, setIsFavoured] = useState(question?.isFavour === 1);
    const [favourCount, setFavourCount] = useState(question?.favourCount || 0);

    // 处理点赞事件
    const handleLike = useCallback(async () => {
        try {
            const res = await (isLiked ? deleteQuestionLikeCountUsingDelete : addQuestionLikeCountUsingPost)({
                questionId: question.id as number
            });

            if (res.data) {
                setIsLiked(prev => !prev);
                setLikeCount(prev => isLiked ? Number(prev) - 1 : Number(prev) + 1);
            }
        } catch (error: any) {
            message.error(error.message);
        }
    }, [isLiked, question.id]);

    // 处理收藏事件
    const handleFavour = useCallback(async () => {
        try {
            const res = await (isFavoured ? deleteQuestionFavourCountUsingDelete : addQuestionFavourCountUsingPost)({
                questionId: question.id as number
            });

            if (res.data) {
                setIsFavoured(prev => !prev);
                setFavourCount(prev => isFavoured ? Number(prev) - 1 : Number(prev) + 1);
            }
        } catch (error: any) {
            message.error(error.message);
        }
    }, [isFavoured, question.id]);

    // 使用 useMemo 缓存 MdViewer 组件
    const contentViewer = useMemo(() => (
        <MdViewer value={question.content} />
    ), [question.content]);

    const answerViewer = useMemo(() => (
        <MdViewer value={question.answer} />
    ), [question.answer]);

    return (
        <div className="question-card">
            <Card>
                <Title level={1} style={{ fontSize: 24 }}>
                    {question.title}
                </Title>
                <TagList tagList={question.tagList} />
                <div style={{ marginBottom: 16 }} />
                {contentViewer}
                <div style={{
                    marginTop: 12,     // 上边距
                    borderTop: '1px solid #f0f0f0',
                    paddingTop: 10,    // 内边距
                    marginBottom: -10  // 与卡片底部的距离
                }}>
                    <Space size={16}>
                        <Button
                            type="text"
                            icon={<LikeOutlined style={{ color: isLiked ? '#faad14' : undefined }} />}
                            onClick={handleLike}
                        >
                            点赞 {likeCount}
                        </Button>
                        <Button
                            type="text"
                            icon={<StarOutlined style={{ color: isFavoured ? '#faad14' : undefined }} />}
                            onClick={handleFavour}
                        >
                            收藏 {favourCount}
                        </Button>
                    </Space>
                </div>
            </Card>
            <div style={{ marginBottom: 16 }} />
            <Card title="推荐答案">
                {answerViewer}
            </Card>
        </div>
    );
};

export default QuestionCard;