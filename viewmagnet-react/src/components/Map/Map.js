import React, { Fragment } from 'react'
import mapboxgl from 'mapbox-gl';
import './Map.css'
import 'mapbox-gl/dist/mapbox-gl.css'

mapboxgl.accessToken = 'pk.eyJ1IjoibG9yZW0tYml0c2luYXJ0aWtzdW0iLCJhIjoiY2s3ZDRxdjQ3MGs1djNtcGFsMXdvMXN4biJ9.2VnL0VNmftEOBnYh-x2Gsw';

class Map extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            lng: 32.7977182,
            lat: 39.92125,
            zoom: 5
        };
    }

    componentDidMount() {
        const map = new mapboxgl.Map({
            container: this.mapContainer,
            style: 'mapbox://styles/mapbox/streets-v11',
            center: [this.state.lng, this.state.lat],
            zoom: this.state.zoom
        });

        map.on('move', () => {
            this.setState({
                lng: map.getCenter().lng.toFixed(4),
                lat: map.getCenter().lat.toFixed(4),
                zoom: map.getZoom().toFixed(2)
            });
        });

        var geojson = {
            type: 'FeatureCollection',
            features: [{
                type: 'Feature',
                geometry: { type: 'Point', coordinates: [32.4825798, 39.9030394] },
                properties: { title: 'Ank', description: 'yo' }
            },
            {
                type: 'Feature',
                geometry: { type: 'Point', coordinates: [28.8720968, 41.0054958] },
                properties: { title: 'İst', description: 'wazzap' }
            }]
        };

        geojson.features.forEach(function (marker) {
            var el = document.createElement('div');
            el.className = 'marker';
            new mapboxgl.Marker(el)
                .setLngLat(marker.geometry.coordinates)
                .setPopup(new mapboxgl.Popup({ offset: 25 })
                    .setHTML('<h3>' + marker.properties.title + '</h3><p>' + marker.properties.description + '</p>'))
                .addTo(map);
        });
    }

    render() {
        return (
            <div>
                <div className='sidebarStyle'>
                    <div>Longitude: {this.state.lng} | Latitude: {this.state.lat} | Zoom: {this.state.zoom}</div>
                </div>
                <div ref={el => this.mapContainer = el} className='mapContainer' />
            </div>
        )
    }
}
export default Map;