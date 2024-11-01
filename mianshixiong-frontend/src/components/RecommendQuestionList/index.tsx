"use client";
import { Card, List } from "antd";
import TagList from "@/components/TagList";
import Link from "next/link";
import "./index.css";

interface Props {
    questionList: API.RecommendQuestionVo[];
}

/**
 * 题目列表组件
 * @param props
 * @constructor
 */
const RecommendQuestionList = (props: Props) => {
    const { questionList = [] } = props;

    return (
        <List
            size="small"  // 添加 size="small" 属性
            dataSource={questionList}
            className="recommend-question-list"  // 添加自定义类名
            renderItem={(item) => (
                <List.Item>
                    <List.Item.Meta
                        title={
                            <Link
                                href={`/question/${item.id}`}
                            >
                                {item.title}
                            </Link>
                        }
                    />
                </List.Item>
            )}
        />
    );
};

export default RecommendQuestionList;
