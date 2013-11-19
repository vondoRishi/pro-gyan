package beans;

import org.apache.log4j.Logger;


public class ExperimentDataBean {
	private static Logger log = Logger.getLogger(ExperimentDataBean.class);
	private String name ;
	private String description;
	private String pos_data;
	private String neg_data;
	private String pos_label = "Positive";
	private String neg_label = "Negative";
	private int pos_instances = 0;
	private int neg_instances = 0;
	
	public int getPos_instances() {
		return pos_instances;
	}
	public void setPos_instances(int pos_instances) {
		this.pos_instances = pos_instances;
	}
	
	public int getNeg_instances() {
		return neg_instances;
	}
	public void setNeg_instances(int neg_instances) {
		this.neg_instances = neg_instances;
	}
	private boolean isNew=true;
	
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPos_data() {
		return pos_data;
	}
	public void setPos_data(String pos_data) {
		this.pos_data = pos_data;
	}
	public String getNeg_data() {
		return neg_data;
	}
	public void setNeg_data(String neg_data) {
		this.neg_data = neg_data;
	}
	public String getPos_label() {
		return pos_label;
	}
	public void setPos_label(String pos_label) {
		this.pos_label = pos_label;
	}
	public String getNeg_label() {
		return neg_label;
	}
	public void setNeg_label(String neg_label) {
		this.neg_label = neg_label;
	}
	
	
}

