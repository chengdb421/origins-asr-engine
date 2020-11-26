/**
 * @(#) Ref.java ASR引擎
 */
package com.origins.utils;

import lombok.Data;

/**
 * @author 智慧工厂@M
 *
 */
@Data
public class Ref<R> {
	private R reference;

	public void release() {
		this.setReference(null);
	}

	public boolean isFree() {
		return getReference() == null;
	}

	public boolean isNotFree() {
		return !isFree();
	}
}
