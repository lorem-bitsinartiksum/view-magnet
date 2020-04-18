import React, { useState, useEffect } from "react";
import { Button, Row, Input, Select } from "antd";
import "./Billboard.css"
import axios from 'axios'
import useBillboards from "./useBillboards";
import ButtonGroup from "antd/lib/button/button-group";

const { Option } = Select;

export default function Billboard({ id, position, status, adId }) {

    let [ads, setAds] = useState([]);
    let [currentAd, setCurrentAd] = useState(null);
    let [selectedAd, setSelectedAd] = useState(null);
    let { shutdownBillboard, interactWithQR, showAd } = useBillboards();

    useEffect(() => {
        let url = 'http://localhost:7000/api/ads';
        axios.get(url, { headers: { 'Authorization': localStorage.getItem('token') } })
            .then((res) => {
                setAds({
                    data: res.data.ads,
                    options: res.data.ads.map((ad, index) => (
                        <Option key={index} value={index}>{ad.title}</Option>
                    ))
                });
                setCurrentAd(res.data.ads.find(ad => ad.id === adId).content)
            })
            .catch((err) => console.log(err))
    }, [adId]);

    return (
        <div className="billboard">
            <div className="poster">
                <img src={currentAd} width="200px" height="400px" />
            </div>
            <div>
                <div>
                    <Row>
                        <br></br>
                        <h3 className="header">
                            {id}
                        </h3>
                    </Row>
                    <Row>
                        <br></br>
                        <ButtonGroup>
                            <Button>Status: </Button>
                            <Button>{status}</Button>
                        </ButtonGroup>
                    </Row>
                    <Row>
                        <br></br>
                        <Input addonBefore="QR/sec:" defaultValue={0} onChange={(e) => interactWithQR({ bid: id, adid: adId, mode: "real" }, e.target.value)} />
                    </Row>
                    <Row>
                        <br></br>
                        <Select style={{ width: 120 }} onChange={(e) => setSelectedAd(ads.data[e])}>
                            {ads.options}
                        </Select>
                        <Button type="primary" onClick={() => showAd(id, selectedAd)}>Show</Button>
                    </Row>
                    <Row>
                        <br></br>
                        <Button type="danger" style={{ width: "100%" }} onClick={() => shutdownBillboard(id)}>Shutdown Billboard</Button>
                    </Row>
                </div>
            </div>
        </div >
    )
}
