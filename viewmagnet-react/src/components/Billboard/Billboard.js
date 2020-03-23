import React, {useState} from "react";
import {Button, Dropdown} from "antd";
import "./Billboard.css"
import {CompactPicker} from "react-color"

export default function Billboard({id, position, status, interest, ad}) {

    let [color, setColor] = useState(mapInterestToColor(interest));

    let changeInterest = () => {
        console.log("send change request")
    };

    return (
        <div className="billboard">
            <div className="poster" style={{background: color.hex}}/>
            <h2 className="header">
                {id}
            </h2>
            <div className="info">
                <Info label="Status" info={status}/>
                <span>Interest:</span>
                <Dropdown
                    overlay={<CompactPicker
                        color={color.rgb}
                        onChangeComplete={(color, _) => {
                            color.rgb.a = null;
                            setColor(color);
                        }}/>}
                    trigger={['click']}>
                    <Button className="ant-dropdown-link" onClick={e => e.preventDefault()}
                            style={{color: color.hex}}>
                        {Object.values(color.rgb).join(" ")}
                    </Button>
                </Dropdown>
            </div>
        </div>
    )
}

function Info({label, info}) {
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

function rgbToHex(rgb) {
    let hex = Number(rgb).toString(16);
    if (hex.length < 2) {
        hex = "0" + hex;
    }
    return hex;
}

function mapInterestToColor(interest) {
    let rgb = {r: interest[0] * 255, g: interest[1] * 255, b: interest[2] * 255};
    let hex = "#" + rgbToHex(rgb.r) + rgbToHex(rgb.g) + rgbToHex(rgb.b);
    return {rgb, hex};
}