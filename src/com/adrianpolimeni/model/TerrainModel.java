package com.adrianpolimeni.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.adrianpolimeni.main.Area;

public class TerrainModel {
	
	Area areaData;
	
	public TerrainModel(Area areaData) {
		if(areaData.hasUnknowns)
			areaData.fillUnknowns();
		this.areaData = areaData;
	}
	
	
	public boolean writeObj(float west, float east, float north, float south) {
		short[][] data = areaData.getHeightData();
		BufferedWriter output = null;
		if(west>east || east>1 || west<0 || north>south || south>1 || north < 0)
			return false;
			
		int left =  (int)(data[0].length * west);
		int right = (int)(data[0].length * east)-1;
		int top = (int)(data[0].length * north );
		int bottom = (int)(data[0].length * south)-1;
				
		
		String area = "_"+Float.toString(west)+
				"-"+Float.toString(east)+
				"-"+Float.toString(north)+
				"-"+Float.toString(south);
		
		try {
	        File file = new File("out/objects/"+areaData.toString()+area+".obj");
	        output = new BufferedWriter(new FileWriter(file));
	        helperWriter(data,output,left,right,top, bottom);
	        
	    } catch ( IOException e ) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        if ( output != null )
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;		
				}
	    }	
		return true;
	}
	
	private void helperWriter(short[][] data, BufferedWriter out, int left, int right, int top, int bottom) throws IOException {
		out.write("# "+areaData.toString()+".obj\n");
		out.write("g "+areaData.toString()+"_plane\n");
		out.write("# The following values are 1/90 times smaller than real world values (in meters)\n");
		for(int j = left;j<right;j++){
	    		for(int i = top;i<bottom;i++) {
	    			out.write("v "+i+" "+(((float)data[i][j])/90f)+" "+j+"\n");
	    		}
		}
		out.write("\n");
		int width = right-left;
		int height = bottom-top;
		
		int maxValue = (width*(height-1));
		for(int i=1; i< maxValue; i++){
			if(i % height != 0) {
			out.write("f"+faceOut(i,0)+faceOut(i,height)+faceOut(i,1)+"\n");
			out.write("f"+faceOut(i,height)+faceOut(i,height+1)+faceOut(i,1)+"\n");
			}
		}
	}
	
	private String faceOut(int i, int index) {
		return " "+(i+index)+"//"+i;
	}
	
}


