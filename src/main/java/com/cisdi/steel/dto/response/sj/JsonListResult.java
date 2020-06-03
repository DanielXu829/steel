package com.cisdi.steel.dto.response.sj;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author HuangXing
 *
 */

public class JsonListResult<T> extends JsonResult {
	
	private List<T> rows = new ArrayList<T>();
	
	public List<T> getRows() {
		return rows;
	}
	
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
}