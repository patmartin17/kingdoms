package com.rivensoftware.hardcoresmp.event;

import java.util.List;

public interface Event 
{
	String getName();

	List<String> getScoreboardText();

	boolean isActive();
}