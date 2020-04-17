import React, { useState, useEffect } from "react";
import { Button, Row, Input, Select } from "antd";
import "./Billboard.css"
import axios from 'axios'
import useBillboards from "./useBillboards";
import ButtonGroup from "antd/lib/button/button-group";

const { Option } = Select;

export default function Billboard({ id, position, status, interest, ad }) {

    let [ads, setAds] = useState([]);
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
            })
            .catch((err) => console.log(err))
    }, []);

    return (
        <div className="billboard">
            <div className="poster" style={{ background: '#ffffff' }} /> {/* todo update this div with adv's img src */}
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
                        <ButtonGroup>
                            <Button>Ad:</Button>
                            <Button>{ad.id.substring(0, 12)}...</Button>
                        </ButtonGroup>
                    </Row>
                    <Row>
                        <br></br>
                        <Input addonBefore="QR/sec:" defaultValue={0} onChange={(e) => interactWithQR({ bid: id, adid: ad.id, mode: "sim" }, e.target.value)} />
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