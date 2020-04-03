import React, { useState } from "react";
import { Button, Dropdown } from "antd";
import "./Billboard.css"
import { CompactPicker } from "react-color"

export default function Billboard({ id, position, status, interest, ad }) {

    let [adColor, setAdColor] = useState(mapValToColor(ad.content));

    let changeInterest = () => {
        console.log("send change request")
    };

    return (
        <div className="billboard">
            <div className="poster" style={{ background: adColor.hex }} />
            <h2 className="header">
                {id}
            </h2>
            <div className="info">
                <Info label="Status" info={status} />
                <span>Ad:</span>
                <Dropdown
                    overlay={<CompactPicker
                        color={adColor.rgb}
                        onChangeComplete={(clr, _) => {
                            clr.rgb.a = null;
                            setAdColor(clr);
                        }} />}
                    trigger={['click']}>
                    <Button className="ant-dropdown-link" onClick={e => e.preventDefault()}
                        style={{ color: adColor.hex }}>
                        {Object.values(adColor.rgb).join(" ")}
                    </Button>
                </Dropdown>
            </div>
        </div>
    )
}

function Info({ label, info }) {
    return (
        <>
            <span>
                {label}:
        </span>
            <span>
                {info}
            </span>
        </>
    )
}

export function mapValToColor(val) {
    let rgb = { r: val[0] * 255, g: val[1] * 255, b: val[2] * 255 };
    let hex = "#" + ((1 << 24) + (rgb.r << 16) + (rgb.g << 8) + rgb.b).toString(16).slice(1, 7);
    return { rgb, hex };
}