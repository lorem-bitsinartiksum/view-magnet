import React, {useCallback, useRef} from 'react'
import {Map as LMap, Marker, Popup, TileLayer} from "react-leaflet"
import './Map.css'
import useBillboards from "../Billboard/useBillboards";
import Billboard from "../Billboard/Billboard";
import {Modal} from "antd";

const {confirm} = Modal;

function Map() {
    let mapRef = useRef();
    let {billboards, addBillboard} = useBillboards();

    let handleClick = useCallback((e) => {
        confirm({
            title: 'Add a new billboard?',
            content: 'Construct a new billboard on given location.',
            onOk() {
                return new Promise((resolve, reject) => {
                    let {lat, lng} = e.latlng;
                    addBillboard({pos: [lat, lng], interest: [0, 0, 0]});
                    resolve()
                }).catch(() => console.error("Sth went wrong"));
            },
            onCancel() {
            },
        });

    }, []);

    return (
        <LMap ref={mapRef} center={[45.4, -75.7]} zoom={12} onClick={handleClick}>
            <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
            />
            {billboards.map(billboard => (
                <Marker onClick={() => console.count("Marker")} key={billboard.position} position={billboard.position}>
                    <Popup maxWidth="400" maxHeight="auto">
                        <Billboard {...billboard} />
                    </Popup>
                </Marker>))}
        </LMap>)
}

function showConfirm() {

}

export default Map