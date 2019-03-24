# WorldTerrainTool
A Java tool for reading and modifying WebGIS terrain data for a variety of applications.

Link to WebGIS terrain data: http://www.webgis.com/srtm3.html

As a rudimentary build, the features are currently limited.
Currently the software can:
- Decode a .hgt file
- Apply a cubic spline algorithm to estimate unknown areas of the terrain data
- Output an interpretation of the terrain data in the form of an image
- Output a 3D model of the terrain data


Example image of WebGIS terrain with some missing data (outlined in red):
![alt text](https://github.com/adrianpolimeni/WorldTerrainTool/blob/master/out/images/N51W116_Unknowns.png)


Image after cubic spline estimation algorithm:
![alt text](https://github.com/adrianpolimeni/WorldTerrainTool/blob/master/out/images/N51W116_Estimates.png)
