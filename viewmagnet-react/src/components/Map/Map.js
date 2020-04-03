import React, { useCallback, useRef, useState, Fragment } from 'react'
import { Map as LMap, Marker, Popup, TileLayer, Circle } from "react-leaflet"
import './Map.css'
import useBillboards from "../Billboard/useBillboards";
import Billboard from "../Billboard/Billboard";
import { Modal, Button, Row, Col } from "antd";

const { confirm } = Modal;

function Map() {
    let mapRef = useRef();
    let { billboards, addBillboard } = useBillboards();
    const [shouldAdd, setShouldAdd] = useState(false)

    let handleClick = (e) => {
        console.log(shouldAdd)
        if (shouldAdd)
            confirm({
                title: 'Add a new billboard?',
                content: 'Construct a new billboard on given location.',
                onOk() {
                    return new Promise((resolve, reject) => {
                        let { lat, lng } = e.latlng;
                        addBillboard({ pos: [lat, lng], interest: [0, 0, 0] });
                        resolve()
                        setShouldAdd(false)
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
                                <Circle center={billboard.position} radius={5000} />
                            </Fragment>))}
                    </LMap>
                </Col>
                <Col span={2}>
                    <Button onClick={() => setShouldAdd(true)}>Add new Bb</Button>
                </Col>
            </Row>
        </Fragment>)
}

function showConfirm() {

}

export default Map