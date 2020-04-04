import React, { useCallback, useRef, useState, Fragment } from 'react'
import { Map as LMap, Marker, Popup, TileLayer, Circle } from "react-leaflet"
import './Map.css'
import useBillboards from "../Billboard/useBillboards";
import Billboard, { mapValToColor, mapColorToVal } from "../Billboard/Billboard";
import { Modal, Button, Row, Col, Dropdown } from "antd";
import { CompactPicker } from "react-color"

const { confirm } = Modal;

function Map() {
    let mapRef = useRef();
    let { billboards, addBillboard, shutdownBillboard, changeInterest } = useBillboards();
    const [canAdd, setCanAdd] = useState(false)
    const [tempColor, setTempColor] = useState(null)

    let handleClick = (e) => {
        console.log(canAdd)
        if (canAdd)
            confirm({
                title: 'Add a new billboard?',
                content: 'Construct a new billboard on given location.',
                onOk() {
                    return new Promise((resolve, reject) => {
                        let { lat, lng } = e.latlng;
                        addBillboard({ pos: [lat, lng], interest: [0, 0, 0] });
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
                                <Circle center={billboard.position} color={mapValToColor(billboard.interest).hex} radius={5000}
                                    onClick={() => confirm({
                                        title: "You are about to change the interests of the people nearby.",
                                        content: <CompactPicker
                                            onChangeComplete={(clr, _) => {
                                                clr.rgb.a = null;
                                                setTempColor(clr.hex)
                                            }} />,
                                        okType: "danger",
                                        okText: "Save Changes",
                                        onOk: () => changeInterest(billboard.id, mapColorToVal(tempColor).val),
                                    })} />
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