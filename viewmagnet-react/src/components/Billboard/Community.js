import React, {useCallback, useEffect, useState} from "react";
import {Button, Descriptions, Spin} from 'antd'

export default function Community({billboardId}) {

    const {loading, error, data, refresh} = useCommunity(billboardId)

    return (
        <div>
            <Descriptions title={`Community ${billboardId}`}>
                {loading ? <Spin/>
                    : <>
                        <Descriptions.Item label="Visitors">{data.visitorCount}</Descriptions.Item>
                        <Descriptions.Item label="QR Readers">{data.qrCount}</Descriptions.Item>
                    </>}
            </Descriptions>
            <Button loading={loading} type="default" onClick={refresh}>Refresh</Button>
        </div>
    )
}

function useCommunity(billboardId) {
    let [loading, setLoading] = useState(true);
    let [error, setError] = useState(null);
    let [data, setData] = useState({visitorCount: 0, qrCount: 0});

    let refresh = useCallback(() => {
        setLoading(true);
        fetch(`http://localhost:8000/community/${billboardId}`)
            .then(resp => resp.json())
            .then(d => setData(d))
            .catch(err => {
                setLoading(false);
                setError(err)
            })
    }, [billboardId]);

    useEffect(() => refresh(), []);

    return {loading, error, data, refresh}
}