/**
 * @author Pratyush(pratyush@sdrc.co.in), created on 31-Jul-2019
 */
package org.sdrc.cpmisweb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataCollectionModel  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8504966900861006904L;
	public List<DataModel> dataCollection;
	private List<ValueObject> legends;
	private List<String> topPerformers ;	
	private List<String> bottomPerformers ;
	
	public List<DataModel> getDataCollection() {
		return dataCollection;
	}
	public void setDataCollection(List<DataModel> dataCollection) {
		this.dataCollection = dataCollection;
	}
	public List<ValueObject> getLegends() {
		return legends;
	}
	public void setLegends(List<ValueObject> legends) {
		this.legends = legends;
	}
	public List<String> getTopPerformers() {
		return topPerformers;
	}
	public void setTopPerformers(List<String> topPerformers) {
		this.topPerformers = topPerformers;
	}
	public List<String> getBottomPerformers() {
		return bottomPerformers;
	}
	public void setBottomPerformers(List<String> bottomPerformers) {
		this.bottomPerformers = bottomPerformers;
	}
	public DataCollectionModel(){
		dataCollection = new ArrayList<DataModel>();
	}
	@Override
	public String toString() {
		return "UtDataCollection [dataCollection=" + dataCollection
				+ ", legends=" + legends + ", topPerformers=" + topPerformers
				+ ", bottomPerformers=" + bottomPerformers + "]";
	}
	


}