"use client";
import CreateModal from "./components/CreateModal";
import UpdateModal from "./components/UpdateModal";
import {
    batchDeleteQuestionsUsingPost,
    deleteQuestionUsingPost,
    listQuestionByPageUsingPost,
} from "@/api/questionController";
import {PlusOutlined} from "@ant-design/icons";
import type {ActionType, ProColumns} from "@ant-design/pro-components";
import {PageContainer, ProTable} from "@ant-design/pro-components";
import {Button, message, Popconfirm, Space, Table, Typography} from "antd";
import React, {useRef, useState} from "react";
import TagList from "@/components/TagList";
import MdEditor from "@/components/MdEditor";
import UpdateBankModal from "@/app/admin/question/components/UpdateBankModal";
import BatchAddQuestionsToBankModal from "@/app/admin/question/components/BatchAddQuestionsToBankModal";
import BatchRemoveQuestionsFromBankModal from "@/app/admin/question/components/BatchRemoveQuestionsFromBankModal";
import "./index.css";

/**
 * 题目管理页面
 *
 * @constructor
 */
const QuestionAdminPage: React.FC = () => {
    // 是否显示新建窗口
    const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
    // 是否显示更新窗口
    const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
    // 是否显示更新所属题库窗口
    const [updateBankModalVisible, setUpdateBankModalVisible] =
        useState<boolean>(false);
    // 是否显示批量向题库添加题目弹窗
    const [
        batchAddQuestionsToBankModalVisible,
        setBatchAddQuestionsToBankModalVisible,
    ] = useState<boolean>(false);
    // 是否显示批量从题库移除题目弹窗
    const [
        batchRemoveQuestionsFromBankModalVisible,
        setBatchRemoveQuestionsFromBankModalVisible,
    ] = useState<boolean>(false);
    // 当前选中的题目 id 列表
    const [selectedQuestionIdList, setSelectedQuestionIdList] = useState<
        number[]
    >([]);
    const actionRef = useRef<ActionType>();
    // 当前题目点击的数据
    const [currentRow, setCurrentRow] = useState<API.Question>();
    // 用于删除数据后当前页没有数据了，则返回表格上一页
    const [currentPageTotal, setCurrentPageTotal] = useState<number>(0);
    const [currentPage, setCurrentPage] = useState<boolean>(false);

    // 用于传递 onCleanSelected 函数。用于在批量操作题目后，清空批量选择状态
    const cleanSelectedRef = useRef<(event?: MouseEvent) => void>();

    /**
     * 删除节点
     *
     * @param row
     */
    const handleDelete = async (row: API.Question) => {
        const hide = message.loading("正在删除");
        if (!row) return true;
        try {
            await deleteQuestionUsingPost({
                id: row.id as any,
            });
            hide();
            message.success("删除成功");
            actionRef?.current?.reload();
            return true;
        } catch (error: any) {
            hide();
            message.error("删除失败，" + error.message);
            return false;
        }
    };

    /**
     * 批量删除节点
     *
     * @param questionIdList
     */
    const handleBatchDelete = async (questionIdList: number[]) => {
        const hide = message.loading("正在操作");
        try {
            await batchDeleteQuestionsUsingPost({
                questionIdList,
            });
            hide();
            message.success("操作成功");
        } catch (error: any) {
            hide();
            message.error("操作失败，" + error.message);
        }
    };

    /**
     * 表格列配置
     */
    const columns: ProColumns<API.Question>[] = [
        {
            title: "id",
            dataIndex: "id",
            valueType: "text",
            hideInForm: true,
            align: "center"

        },
        {
            title: "所属题库",
            dataIndex: "questionBankId",
            hideInTable: true,
            hideInForm: true,
            align: "center"
        },
        {
            title: "标题",
            dataIndex: "title",
            valueType: "text",
            align: "center"
        },
        {
            title: "内容",
            dataIndex: "content",
            valueType: "text",
            hideInSearch: true,
            align: "center",
            width: 240,
            renderFormItem: (
                _,
                {
                    type,
                    defaultRender,
                    formItemProps,
                    fieldProps,
                    ...rest
                }: any,
                form
            ) => {
                // 编写要渲染的表单项
                // value 和 onchange 会通过 form 自动注入
                return <MdEditor {...fieldProps} />;
            },
        },
        {
            title: "答案",
            dataIndex: "answer",
            valueType: "text",
            hideInSearch: true,
            align: "center",
            width: 640,
            renderFormItem: (
                _,
                {type, defaultRender, formItemProps, fieldProps, ...rest}: any,
                form,
            ) => {
                // 编写要渲染的表单项
                // value 和 onchange 会通过 form 自动注入
                return <MdEditor {...fieldProps} />;
            },
        },
        {
            title: "标签",
            dataIndex: "tags",
            valueType: "select",
            fieldProps: {
                mode: "tags",
            },
            render: (_, record) => {
                const tagList = JSON.parse(record.tags || "[]");
                return <TagList tagList={tagList}/>;
            },
        },
        {
            title: "创建用户",
            dataIndex: "userId",
            valueType: "text",
            hideInForm: true,
        },

        {
            title: "创建时间",
            sorter: true,
            dataIndex: "createTime",
            valueType: "dateTime",
            align: "center",
            hideInSearch: true,
            hideInForm: true,
        },
        // {
        //     title: "编辑时间",
        //     sorter: true,
        //     dataIndex: "editTime",
        //     valueType: "dateTime",
        //     align: "center",
        //     hideInSearch: true,
        //     hideInForm: true,
        // },
        {
            title: "更新时间",
            sorter: true,
            dataIndex: "updateTime",
            valueType: "dateTime",
            align: "center",
            hideInSearch: true,
            hideInForm: true,
        },
        {
            title: "操作",
            dataIndex: "option",
            valueType: "option",
            align: "center",
            render: (_, record) => (
                <Space size="middle">
                    <Typography.Link
                        onClick={() => {
                            setCurrentRow(record);
                            setUpdateModalVisible(true);
                        }}
                    >
                        修改
                    </Typography.Link>
                    <Typography.Link
                        onClick={() => {
                            setCurrentRow(record);
                            setUpdateBankModalVisible(true);
                        }}
                    >
                        修改所属题库
                    </Typography.Link>
                    <Popconfirm
                        title="确认删除?"
                        onConfirm={() => handleDelete(record)}
                        okText="确认"
                        cancelText="取消"
                    >
                        <Typography.Link type="danger">删除</Typography.Link>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <PageContainer>
            <ProTable<API.Question>
                headerTitle={"题目列表"}
                actionRef={actionRef}
                // scroll={{
                //     x: true,
                // }}
                search={{
                    labelWidth: 120,
                }}
                rowKey="id"
                rowSelection={{
                    // 注释该行则默认不显示下拉选项
                    selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
                    // defaultSelectedRowKeys: [1],
                }}
                tableAlertRender={({
                                       selectedRowKeys,
                                       selectedRows,
                                       onCleanSelected,
                                   }) => {
                    // 将onCleanSelected函数存储到cleanSelectedRef中
                    cleanSelectedRef.current = onCleanSelected;
                    return (
                        <Space size={24}>
                            <span>
                                已选 {selectedRowKeys.length} 项
                                <a style={{marginInlineStart: 8}} onClick={onCleanSelected}>
                                    取消选择
                                </a>
                            </span>
                        </Space>
                    );
                }}
                tableAlertOptionRender={({
                                             selectedRowKeys,
                                             selectedRows,
                                             onCleanSelected,
                                         }) => {
                    return (
                        <Space size={16}>
                            <Button
                                onClick={() => {
                                    // 打开弹窗
                                    setSelectedQuestionIdList(selectedRowKeys as number[]);
                                    setBatchAddQuestionsToBankModalVisible(true);
                                }}
                            >
                                批量向题库添加题目
                            </Button>
                            <Button
                                onClick={() => {
                                    // 打开弹窗
                                    setSelectedQuestionIdList(selectedRowKeys as number[]);
                                    setBatchRemoveQuestionsFromBankModalVisible(true);
                                }}
                            >
                                批量从题库移除题目
                            </Button>
                            <Popconfirm
                                title="确认删除"
                                description="你确定要删除这些题目么？"
                                onConfirm={() => {
                                    // 批量删除
                                    handleBatchDelete(selectedRowKeys as number[]);
                                }}
                                okText="确认"
                                cancelText="取消"
                            >
                                <Button
                                    danger
                                    // onClick={() => {
                                    //     // 打开弹窗
                                    // }}
                                >
                                    批量删除题目
                                </Button>
                            </Popconfirm>
                        </Space>
                    );
                }}
                toolBarRender={() => [
                    <Button
                        type="primary"
                        key="primary"
                        onClick={() => {
                            setCreateModalVisible(true);
                        }}
                    >
                        <PlusOutlined/> 新建
                    </Button>,
                ]}
                request={async (params, sort, filter) => {
                    const sortField = Object.keys(sort)?.[0] || "updateTime";
                    const sortOrder = sort?.[sortField] || "descend";

                    //如果当前页的数据已经删除完了，则查询上一页
                    if (currentPage) {
                        params.current = (params.current as any) - 1;
                        setCurrentPage(false);
                    }

                    const {data, code} = await listQuestionByPageUsingPost({
                        ...params,
                        sortField,
                        sortOrder,
                        ...filter,
                    } as API.QuestionQueryRequest);

                    setCurrentPageTotal(Number(data?.total) || 0); // 设置当前页的数据总数

                    return {
                        success: code === 0,
                        data: data?.records || [],
                        total: Number(data?.total) || 0,
                    };
                }}
                columns={columns}
            />
            <CreateModal
                visible={createModalVisible}
                columns={columns}
                onSubmit={() => {
                    setCreateModalVisible(false);
                    actionRef.current?.reload();
                }}
                onCancel={() => {
                    setCreateModalVisible(false);
                }}
            />
            <UpdateModal
                visible={updateModalVisible}
                columns={columns}
                oldData={currentRow}
                onSubmit={() => {
                    setUpdateModalVisible(false);
                    setCurrentRow(undefined);
                    actionRef.current?.reload();
                }}
                onCancel={() => {
                    setUpdateModalVisible(false);
                    actionRef?.current?.reload();
                }}
            />
            <UpdateBankModal
                visible={updateBankModalVisible}
                questionId={currentRow?.id}
                onCancel={() => {
                    setCurrentRow(undefined);
                    setUpdateBankModalVisible(false);
                    actionRef?.current?.reload();

                }}
            />
            <BatchAddQuestionsToBankModal
                visible={batchAddQuestionsToBankModalVisible}
                questionIdList={selectedQuestionIdList}
                onSubmit={() => {
                    setBatchAddQuestionsToBankModalVisible(false);
                    actionRef?.current?.reload();
                    // 调用存储的onCleanSelected函数来清空选择
                    cleanSelectedRef.current?.();
                }}
                onCancel={() => {
                    setBatchAddQuestionsToBankModalVisible(false);
                    actionRef?.current?.reload();
                }}
            />
            <BatchRemoveQuestionsFromBankModal
                visible={batchRemoveQuestionsFromBankModalVisible}
                questionIdList={selectedQuestionIdList}
                onSubmit={() => {
                    setBatchRemoveQuestionsFromBankModalVisible(false);
                    actionRef?.current?.reload();
                    cleanSelectedRef.current?.();
                }}
                onCancel={() => {
                    setBatchRemoveQuestionsFromBankModalVisible(false);
                    actionRef?.current?.reload();
                }}
            />
        </PageContainer>
    );
};
export default QuestionAdminPage;
