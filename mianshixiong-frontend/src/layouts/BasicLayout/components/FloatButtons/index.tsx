import { VerticalAlignTopOutlined, WechatOutlined } from '@ant-design/icons';
import { FloatButton, Modal, Image } from 'antd';
import React, { useState } from 'react';

const FloatButtons: React.FC = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleWechatClick = () => {
        setIsModalOpen(true);
    };

    return (
        <>
            <Modal
                title="扫描二维码添加微信"
                open={isModalOpen}
                onCancel={() => setIsModalOpen(false)}
                footer={null}
            >
                <Image
                    src="/assets/weixin.jpg"
                    alt="微信二维码"
                    style={{ width: '100%' }}
                />
            </Modal>
            <FloatButton.Group
                shape="square"
                style={{ right: 24, bottom: 24 }}
            >
                <FloatButton
                    icon={<WechatOutlined />}
                    onClick={handleWechatClick}
                    tooltip="联系我们"
                />
            </FloatButton.Group>
        </>
    );
};

export default FloatButtons;