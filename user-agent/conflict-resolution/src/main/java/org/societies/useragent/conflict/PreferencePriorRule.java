package org.societies.useragent.conflict;

import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.conflict.ConflictResolutionRule;

public class PreferencePriorRule implements ConflictResolutionRule{

	@Override
	public boolean match(IOutcome intent, IOutcome prefernce) {
		// TODO Auto-generated method stub
		return true;	
	}

	@Override
	public IOutcome tradeoff(IOutcome intention, IOutcome preference) {
		// TODO Auto-generated method stub
		return preference;
	}

}
