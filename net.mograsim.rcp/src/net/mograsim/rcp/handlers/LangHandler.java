
package net.mograsim.rcp.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.nls.ILocaleChangeService;

public class LangHandler
{

	@Inject
	ILocaleChangeService lcs;

	@Execute
	public void execute(@Named("change_lang_locale") String change_lang_locale)
	{
		lcs.changeApplicationLocale(change_lang_locale);
	}

}