package com.katouyi.tools.mongo.mall;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class Page<T> implements Serializable {

	private static final long serialVersionUID = 5760097915453738435L;
	/**
	 * 每页显示个数
	 */
	private int pageSize = 10;
	/**
	 * 当前页数
	 */
	private int currentPage = 1;
	/**
	 * 总页数
	 */
	private int totalPage;
	/**
	 * 总记录数
	 */
	private int totalCount;
	/**
	 * 结果列表
	 */
	private List<T> rows;

	public Page(int currentPage, int pageSize) {
		this.currentPage = currentPage <= 0 ? 1 : currentPage;
		this.pageSize = pageSize <= 0 ? 1 : pageSize;
	}

	/**
	 * 设置结果 及总页数
	 */
	public void build(List<T> rows) {
		this.setRows(rows);
		int count = this.getTotalCount();
		int divisor = count / this.getPageSize();
		int remainder = count % this.getPageSize();
		this.setTotalPage(remainder == 0 ? divisor == 0 ? 1 : divisor : divisor + 1);
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
