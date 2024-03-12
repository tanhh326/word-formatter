import {Map, View} from 'ol';

import {applyTransform, getTopLeft, getWidth} from 'ol/extent';
import TileLayer from 'ol/layer/Tile';
import {get as getProjection, getTransform} from 'ol/proj';
import {WMTS as WMTSSource} from 'ol/source';
import WMTS from 'ol/tilegrid/WMTS';

interface Options {
  tk: string;
  projection?: 'EPSG:4326' | 'EPSG:900913';
  // 经纬度 | 墨卡托
  matrixSet?: 'c' | 'w';
  // 全球境界 | 地形注记 | 地形晕渲 | 影像注记 | 影像底图 | 矢量注记 | 矢量底图
  layer: 'ibo' | 'cta' | 'ter' | 'cia' | 'img' | 'cva' | 'vec';
}

/**
 * @description 获取OpenLayers框架下的ol/layer/Tile类型天地图图层
 * @param options
 */
export function getTianDiTuTile(options: Options) {
  const {tk, projection = 'EPSG:4326', matrixSet = 'c', layer} = options;

  const theProjection: any = getProjection(projection);
  const projectionExtent = theProjection.getExtent();
  const origin = projectionExtent ? getTopLeft(projectionExtent) : [-180, 90];
  const fromLonLat = getTransform(projection, theProjection);
  const width = projectionExtent
    ? getWidth(projectionExtent)
    : getWidth(applyTransform([-180.0, -90.0, 180.0, 90.0], fromLonLat));
  const resolutions = [];
  const matrixIds: any = [];
  for (let z = 1; z < 19; z += 1) {
    resolutions[z] = width / (256 * 2 ** z);
    matrixIds[z] = z;
  }

  const wmtsSource = new WMTSSource({
    url: `https://t{0-7}.tianditu.gov.cn/${layer}_${matrixSet}/wmts?tk=${tk}`,
    layer,
    matrixSet,
    projection,
    version: '1.0.0',
    format: 'tiles',
    requestEncoding: 'KVP',
    style: 'default',
    tileGrid: new WMTS({
      origin,
      resolutions,
      matrixIds,
    }),
  });

  return new TileLayer({
    source: wmtsSource,
  });
}


export default function useBaseMap() {
  const mapInstance = new Map({
    controls: [],
    view: new View({
      projection: 'EPSG:4326',
      zoom: 16,
      center: [117.2, 28.11],
      //淳安县区域范围：extent: [117.2, 28.11, 120.2, 31.02],
    }),
  });


  const tk = 'b98b719567edc11061cd5c24778dbaa1';
  mapInstance.addLayer(
    getTianDiTuTile({
      layer: 'img',
      tk: tk,
    }),
  );
  mapInstance.addLayer(
    getTianDiTuTile({
      layer: 'cia',
      tk: tk,
    }),
  );

  return {
    mapInstance,
  };
}
