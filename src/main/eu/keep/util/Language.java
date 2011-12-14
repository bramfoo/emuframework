/*
* $Revision$ $Date$
* $Author$
* $header:
* Copyright (c) 2009-2011 Tessella plc.
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* For more information about this project, visit
*   http://www.keep-project.eu/
*   http://emuframework.sourceforge.net/
* or contact us via email:
*   blohman at users.sourceforge.net
*   edonoordermeer at users.sourceforge.net
*   dav_m at users.sourceforge.net
*   bkiers at users.sourceforge.net
* Developed by:
*   Tessella plc <www.tessella.com>
*   Koninklijke Bibliotheek <www.kb.nl>
*   KEEP <www.keep-project.eu>
* Project Title: Core Emulation Framework (Core EF)$ 
*/
package eu.keep.util;

//import eu.keep.emulatorarchive.emulatorpackage.EmuLanguage;
//import eu.keep.softwarearchive.pathway.SwLanguage;

/**
 * Utility class to hold language details.
 * @author nooe
 */
public enum Language {

	// From http://www.loc.gov/standards/iso639-2/php/English_list.php
	undefined(null, null),
	aa("aa", "Afar"),
	ab("ab", "Abkhazian"),
	ae("ae", "Avestan"),
	af("af", "Afrikaans"),
	ak("ak", "Akan"),
	am("am", "Amharic"),
	an("an", "Aragonese"),
	ar("ar", "Arabic"),
	as("as", "Assamese"),
	av("av", "Avaric"),
	ay("ay", "Aymara"),
	az("az", "Azerbaijani"),
	ba("ba", "Bashkir"),
	be("be", "Belarusian"),
	bg("bg", "Bulgarian"),
	bh("bh", "Bihari languages"),
	bi("bi", "Bislama"),
	bm("bm", "Bambara"),
	bn("bn", "Bengali"),
	bo("bo", "Tibetan"),
	br("br", "Breton"),
	bs("bs", "Bosnian"),
	ca("ca", "Catalan"),
	ce("ce", "Chechen"),
	ch("ch", "Chamorro"),
	co("co", "Corsican"),
	cr("cr", "Cree"),
	cs("cs", "Czech"),
	cu("cu", "Church Slavic"),
	cv("cv", "Chuvash"),
	cy("cy", "Welsh"),
	da("da", "Danish"),
	de("de", "Deutsch"),
	dv("dv", "Dhivehi"),
	dz("dz", "Dzongkha"),
	ee("ee", "Ewe"),
	el("el", "Greek, Modern (1453-)"),
	en("en", "English"),
	eo("eo", "Esperanto"),
	es("es", "Spanish"),
	et("et", "Estonian"),
	eu("eu", "Basque"),
	fa("fa", "Persian"),
	ff("ff", "Fulah"),
	fi("fi", "Finnish"),
	fj("fj", "Fijian"),
	fo("fo", "Faroese"),
    fr("fr", "Fran\u00E7ais"),
	fy("fy", "Western Frisian"),
	ga("ga", "Irish"),
	gd("gd", "Gaelic"),
	gl("gl", "Galician"),
	gn("gn", "Guarani"),
	gu("gu", "Gujarati"),
	gv("gv", "Manx"),
	ha("ha", "Hausa"),
	he("he", "Hebrew"),
	hi("hi", "Hindi"),
	ho("ho", "Hiri Motu"),
	hr("hr", "Croatian"),
	ht("ht", "Haitian"),
	hu("hu", "Hungarian"),
	hy("hy", "Armenian"),
	hz("hz", "Herero"),
	ia("ia", "Interlingua"),
	id("id", "Indonesian"),
	ie("ie", "Occidental"),
	ig("ig", "Igbo"),
	ii("ii", "Sichuan Yi"),
	ik("ik", "Inupiaq"),
	io("io", "Ido"),
	is("is", "Icelandic"),
	it("it", "Italian"),
	iu("iu", "Inuktitut"),
	ja("ja", "Japanese"),
	jv("jv", "Javanese"),
	ka("ka", "Georgian"),
	kg("kg", "Kongo"),
	ki("ki", "Kikuyu"),
	kj("kj", "Kwanyama"),
	kk("kk", "Kazakh"),
	kl("kl", "Kalaallisut"),
	km("km", "Central Khmer"),
	kn("kn", "Kannada"),
	ko("ko", "Korean"),
	kr("kr", "Kanuri"),
	ks("ks", "Kashmiri"),
	ku("ku", "Kurdish"),
	kv("kv", "Komi"),
	kw("kw", "Cornish"),
	ky("ky", "Kyrgyz"),
	la("la", "Latin"),
	lb("lb", "Letzeburgesch"),
	lg("lg", "Ganda"),
	li("li", "Limburger"),
	ln("ln", "Lingala"),
	lo("lo", "Lao"),
	lt("lt", "Lithuanian"),
	lu("lu", "Luba-Katanga"),
	lv("lv", "Latvian"),
	mg("mg", "Malagasy"),
	mh("mh", "Marshallese"),
	mi("mi", "Maori"),
	mk("mk", "Macedonian"),
	ml("ml", "Malayalam"),
	mn("mn", "Mongolian"),
	mr("mr", "Marathi"),
	ms("ms", "Malay"),
	mt("mt", "Maltese"),
	my("my", "Burmese"),
	na("na", "Nauru"),
	nb("nb", "Norwegian Bokm\u00E5l"),
	nd("nd", "North Ndebele"),
	ne("ne", "Nepali"),
	ng("ng", "Ndonga"),
	nl("nl", "Nederlands"),
	nn("nn", "Norwegian Nynorsk"),
	no("no", "Norwegian"),
	nr("nr", "South Ndebele"),
	nv("nv", "Navaho"),
	ny("ny", "Chichewa"),
	oc("oc", "Occitan (post 1500)"),
	oj("oj", "Ojibwa"),
	om("om", "Oromo"),
	or("or", "Oriya"),
	os("os", "Ossetian"),
	pa("pa", "Punjabi"),
	pi("pi", "Pali"),
	pl("pl", "Polish"),
	ps("ps", "Pashto"),
	pt("pt", "Portuguese"),
	qu("qu", "Quechua"),
	rm("rm", "Romansh"),
	rn("rn", "Rundi"),
	ro("ro", "Romanian"),
	ru("ru", "Russian"),
	rw("rw", "Kinyarwanda"),
	sa("sa", "Sanskrit"),
	sc("sc", "Sardinian"),
	sd("sd", "Sindhi"),
	se("se", "Northern Sami"),
	sg("sg", "Sango"),
	si("si", "Sinhala"),
	sk("sk", "Slovak"),
	sl("sl", "Slovenian"),
	sm("sm", "Samoan"),
	sn("sn", "Shona"),
	so("so", "Somali"),
	sq("sq", "Albanian"),
	sr("sr", "Serbian"),
	ss("ss", "Swati"),
	st("st", "Sotho, Southern"),
	su("su", "Sundanese"),
	sv("sv", "Swedish"),
	sw("sw", "Swahili"),
	ta("ta", "Tamil"),
	te("te", "Telugu"),
	tg("tg", "Tajik"),
	th("th", "Thai"),
	ti("ti", "Tigrinya"),
	tk("tk", "Turkmen"),
	tl("tl", "Tagalog"),
	tn("tn", "Tswana"),
	to("to", "Tonga (Tonga Islands)"),
	tr("tr", "Turkish"),
	ts("ts", "Tsonga"),
	tt("tt", "Tatar"),
	tw("tw", "Twi"),
	ty("ty", "Tahitian"),
	ug("ug", "Uyghur"),
	uk("uk", "Ukrainian"),
	ur("ur", "Urdu"),
	uz("uz", "Uzbek"),
	ve("ve", "Venda"),
	vi("vi", "Vietnamese"),
	vo("vo", "Volap\u00FCk"),
	wa("wa", "Walloon"),
	wo("wo", "Wolof"),
	xh("xh", "Xhosa"),
	yi("yi", "Yiddish"),
	yo("yo", "Yoruba"),
	za("za", "Zhuang"),
	zh("zh", "Chinese"),
	zu("zu", "Zulu");
	
    private final String languageId;
	private final String languageName;

	/**
	 * Constructor
	 * @param languageId the languageId
	 * @param languageName the language Name
	 */
	private Language(String languageId, String languageName) {
		this.languageId = languageId;
		this.languageName = languageName;
	}
	
	public String getLanguageId() {
		return languageId;
	}

	public String getLanguageName() {
		return languageName;
	}

    @Override
    public String toString() {
        return languageName;
    }
}
