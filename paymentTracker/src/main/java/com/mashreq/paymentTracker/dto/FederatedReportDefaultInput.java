package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;

public class FederatedReportDefaultInput implements Serializable{

	private static final long serialVersionUID = 3160742869431264893L;
	private ReportComponentDTO component;
	private List<ReportPromptsInstanceDTO> instancePrompts;
	private boolean transpose;

	public ReportComponentDTO getComponent() {
		return component;
	}

	public void setComponent(ReportComponentDTO component) {
		this.component = component;
	}

	public List<ReportPromptsInstanceDTO> getInstancePrompts() {
		return instancePrompts;
	}

	public void setInstancePrompts(List<ReportPromptsInstanceDTO> instancePrompts) {
		this.instancePrompts = instancePrompts;
	}

	public boolean isTranspose() {
		return transpose;
	}

	public void setTranspose(boolean transpose) {
		this.transpose = transpose;
	}

	@Override
	public String toString() {
		return "FederatedReportDefaultInput [component=" + component + ", instancePrompts=" + instancePrompts
				+ ", transpose=" + transpose + "]";
	}

}
