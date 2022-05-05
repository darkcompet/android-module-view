/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.view;

import android.text.Html;
import android.text.Spanned;

public class DkHtmlBuilder {
	private final StringBuilder builder;

	public DkHtmlBuilder(String text) {
		this(text, text.length() + 128);
	}

	public DkHtmlBuilder newIns(String text) {
		return new DkHtmlBuilder(text, text.length() + 128);
	}

	public DkHtmlBuilder(String text, int capacity) {
		this.builder = new StringBuilder(capacity);
		this.builder.append(text);
	}

	public DkHtmlBuilder bold() {
		builder.insert(0, "<b>").append("</b>");
		return this;
	}

	public DkHtmlBuilder color(int color) {
		String hexColor = String.format("#%06X", (0xFFFFFF & color));
		builder.insert(0, "<font color=\"" + hexColor + "\">").append("</font>");
		return this;
	}

	public DkHtmlBuilder underline() {
		builder.insert(0, "<u>").append("</u>");
		return this;
	}

	public Spanned build() {
		return Html.fromHtml(builder.toString());
	}
}
