import React, { useRef, useState, Fragment } from 'react'
import { Map as LMap, Marker, Popup, TileLayer, Circle } from "react-leaflet"
import './Map.css'
import useBillboards from "../Billboard/useBillboards";
import Billboard from "../Billboard/Billboard";
import { Modal, Button, Row, Col, InputNumber, Input } from "antd";

const { confirm } = Modal;

function Map() {
    let mapRef = useRef();
    let { billboards, addBillboard, changeInterest } = useBillboards();
    const [canAdd, setCanAdd] = useState(false)

    let handleClick = (e) => {
        let features = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.5]
        if (canAdd)
            confirm({
                title: 'Add a new billboard?',
                content: <Fragment>Construct a new billboard on given location with following interest values.
                    <br />
                    <br />
                    <InputNumber placeholder="price" onChange={(str) => features[0] = parseFloat(str)} />
                    <br />
                    <br />
                    <Input.Group>
                        <InputNumber placeholder="baby" onChange={(str) => features[1] = parseFloat(str)} />
                        <InputNumber placeholder="child" onChange={(str) => features[2] = parseFloat(str)} />
                        <InputNumber placeholder="young" onChange={(str) => features[3] = parseFloat(str)} />
                        <InputNumber placeholder="adult" onChange={(str) => features[4] = parseFloat(str)} />
                        <InputNumber placeholder="elder" onChange={(str) => features[5] = parseFloat(str)} />
                    </Input.Group>
                    <br />
                    <InputNumber placeholder="rainy" onChange={(str) => features[6] = parseFloat(str)} />
                    <InputNumber placeholder="sunny" onChange={(str) => features[7] = parseFloat(str)} />
                    <br />
                    <br />
                    <InputNumber placeholder="cold" onChange={(str) => features[8] = parseFloat(str)} />
                    <InputNumber placeholder="hot" onChange={(str) => features[9] = parseFloat(str)} />
                    <br />
                    <br />
                    <InputNumber placeholder="wview" onChange={(str) => features[10] = parseFloat(str)} />
                </Fragment>,
                onOk() {
                    return new Promise((resolve, reject) => {
                        let { lat, lng } = e.latlng;
                        addBillboard({ pos: [lat, lng], interest: features });
                        resolve()
                        setCanAdd(false)
                    }).catch(() => console.error("Sth went wrong"));
                },
                onCancel() {
                },
            });
    }

    return (
        <Fragment>
            <Row>
                <Col span={22}>
                    <LMap ref={mapRef} center={[45.4, -75.7]} zoom={12} onClick={handleClick}>
                        <TileLayer
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        />
                        {billboards.map(billboard => (
                            <Fragment>
                                <Marker key={billboard.position} position={billboard.position}>
                                    <Popup maxWidth="400" maxHeight="auto">
                                        <Billboard {...billboard} />
                                    </Popup>
                                </Marker>
                                <Circle center={billboard.position} radius={5000}
                                    onClick={() => {
                                        let features = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.5]
                                        confirm({
                                            title: "You are about to change the interests of the people nearby.",
                                            content: <Fragment>
                                                <br />
                                                <InputNumber placeholder="price" onChange={(str) => features[0] = parseFloat(str)} />
                                                <br />
                                                <br />
                                                <Input.Group>
                                                    <InputNumber placeholder="baby" onChange={(str) => features[1] = parseFloat(str)} />
                                                    <InputNumber placeholder="child" onChange={(str) => features[2] = parseFloat(str)} />
                                                    <InputNumber placeholder="young" onChange={(str) => features[3] = parseFloat(str)} />
                                                    <InputNumber placeholder="adult" onChange={(str) => features[4] = parseFloat(str)} />
                                                    <InputNumber placeholder="elder" onChange={(str) => features[5] = parseFloat(str)} />
                                                </Input.Group>
                                                <br />
                                                <InputNumber placeholder="rainy" onChange={(str) => features[6] = parseFloat(str)} />
                                                <InputNumber placeholder="sunny" onChange={(str) => features[7] = parseFloat(str)} />
                                                <br />
                                                <br />
                                                <InputNumber placeholder="cold" onChange={(str) => features[8] = parseFloat(str)} />
                                                <InputNumber placeholder="hot" onChange={(str) => features[9] = parseFloat(str)} />
                                                <br />
                                                <br />
                                                <InputNumber placeholder="wview" onChange={(str) => features[10] = parseFloat(str)} />
                                            </Fragment>,
                                            okType: "danger",
                                            okText: "Save Changes",
                                            onOk: () => changeInterest(billboard.id, features)

                                        });
                                    }} />
                            </Fragment>))}
                    </LMap>
                </Col>
                <Col span={2}>
                    <Button onClick={() => setCanAdd(true)}>Add new Bb</Button>
                </Col>
            </Row>
        </Fragment>)
}

export default Map