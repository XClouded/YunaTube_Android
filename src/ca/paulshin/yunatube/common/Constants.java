package ca.paulshin.yunatube.common;

public class Constants {
	// Common
	public static final String CLIP_THUMBNAIL_URL = "http://i.ytimg.com/vi/%s/2.jpg";
	public static final String CLIP_HQ_THUMBNAIL_URL = "http://i.ytimg.com/vi/%s/hqdefault.jpg";

	// Settings
	public static final String NOTIFICATION = "notification";
	public static final String NICKNAME = "nickname";
	public static final String CHAT_STICKER_OFF = "sticker_off";

	// Main
	public static final String NEWS_URL = "http://paulshin.ca/yunatube/res/yuna_news_%s.html";
	public static final String MAIN_URL = "http://www.paulshin.ca/yunatube/res/main_%s.json";
	public static final String NEW_CLIPS = "http://paulshin.ca/yunatube/res/youtube/get_new_clips.php";
	public static final String YUNAFACT = "https://twitter.com/YunaKim_Facts";

	// About Yuna
	public static final String YUNAKIM_COM = "http://www.yunakim.com";
	public static final String YUNA_ISU_RESULTS = "http://www.isuresults.com/bios/isufs_cr_00007232.htm";
	public static final String AB_YN_20Q20A = "http://paulshin.ca/yunatube/res/about_yuna/20q20a_%s.html";
	public static final String AB_YN_PROGRAMS = "http://paulshin.ca/yunatube/res/about_yuna/programs.html";
	public static final String AB_YN_COMPETITIONS = "http://paulshin.ca/yunatube/res/about_yuna/competitions_%s.html";
	public static final String AB_YN_PRAISES = "http://paulshin.ca/yunatube/res/about_yuna/praises_%s.html";
	public static final String AB_YN_AWARDS = "http://paulshin.ca/yunatube/res/about_yuna/awards_%s.html";
	public static final String AB_YN_CHARITIES = "http://paulshin.ca/yunatube/res/about_yuna/charities_%s.html";

	// Yuna on the web
	public static final String SEARCH_URL_YOUTUBE = "http://m.youtube.com/results?gl=RU&client=mv-google&hl=en&q=%s&submit=Search";
	public static final String SEARCH_URL_GOOGLE = "https://www.google.com/?q=%s#hl=en&newwindow=1&output=search&sclient=psy-ab&q=%s&oq=%s";
	public static final String SEARCH_URL_DAUM = "http://m.search.daum.net/search?w=tot&nil_mtopsearch=btn&q=%s";
	public static final String SEARCH_URL_NAVER = "http://m.search.naver.com/search.naver?query=%s&where=m&sm=mtp_hty";
	public static final String SEARCH_URL_BING = "http://m.bing.com/search?q=%s&FORM=BLXBSS&btsrc=internal";
	public static final String SEARCH_URL_NATE = "http://m.search.nate.com/search/all.html?q=%s";

	public static final String NEWS_URL_GOOGLE = "https://www.google.ca/search?q=%s&tbm=nws";
	public static final String NEWS_URL_DAUM = "http://m.search.daum.net/search?w=news&q=%s&sort=1&cluster=n";
	public static final String NEWS_URL_NAVER = "http://m.news.naver.com/search.nhn?searchType=newest&searchQuery=%s";
	public static final String NEWS_URL_NATE = "http://m.search.nate.com/search/all.html?q=%s&ssn=036";
	public static final String NEWS_URL_BING = "http://www.bing.com/news/search?q=%s&FORM=HDRSC6";

	// Album
	public static final String ALBUM_FLICKR_COLLECTIONS_GETTREE = "http://api.flickr.com/services/rest/?method=flickr.collections.getTree&api_key=044ffe6c8bf9e9c99b512f195d45a628&user_id=52789087@N05&format=json&nojsoncallback=1";
	public static final String ALBUM_FLICKR_PHOTOSETS_GETPHOTO = "http://api.flickr.com/services/rest/?method=flickr.photosets.getPhotos&api_key=044ffe6c8bf9e9c99b512f195d45a628&photoset_id=%s&format=json&per_page=500&nojsoncallback=1";
	public static final String ALBUM_FLICKR_PHOTOSETS_GETCORVER = "http://paulshin.ca/yunatube/res/set_thumbnail.php?set_id=%s";
	public static final String ALBUM_PHOTO_URL = "http://farm%s.staticflickr.com/%s/%s_%s_%s.jpg";
	public static final String ALBUM_MY_DIR = "YuNaTube";

	public static final String FILE_CACHE_DIR = "YuNaTubeTemp";

	public static final String GIF_LIST_URL = "http://paulshin.ca/yunatube/phpThumb/gifimages.php";
	public static final String GIF_LOADING_IMAGE_URL = "http://paulshin.ca/yunatube/phpThumb/loading.gif";
	public static final String GIF_THUMBNAIL_LIST_URL = "http://paulshin.ca/yunatube/phpThumb/phpThumb.php?src=gifs/%s&w=%d&h=%d&hash=%s";
	public static final String GIF_FOLDER_URL = "http://gifs.yunatube.com/";
	public static final String GIF_MY_DIR = "YuNaTubeGifs";

	// Count
	public static final String YOUNATUBE_COUNTER_URL = "http://www.paulshin.ca/yunatube/counter.php";

	// Survey
	public static final String SURVEY_URL = "http://paulshin.ca/yunatube/res/survey/index.php?deviceId=";

	// YouTube
	public static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyB4fv0_D1_ZYuQxD6uFK7K3D6oHbVlxIi4";
	public static final String YOUTUBE_COMMENT_RETRIEVE = "http://paulshin.ca/yunatube/res/comment/getmsgs.php";
	public static final String YOUTUBE_COMMENT_SUBMIT = "http://paulshin.ca/yunatube/res/comment/submitmsg.php";

	// GCM
	public static final String GCM_SENDER_ID = "848643795806";
	public static final String GCM_SENDER_REGID_URL = "http://paulshin.ca/yunatube/res/dbaction.php?action=insert&rid=";
	public static final String GCM_SENDER_REGID_TEST_URL = "http://paulshin.ca/yunatube/res/dbaction.php?action=insert&rid=";

	// Message for Yuna
	public static final String MESSAGE_RETRIEVE = "http://paulshin.ca/yunatube/res/toyuna/getmsgs.php";
	public static final String MESSAGE_SUBMIT = "http://paulshin.ca/yunatube/res/toyuna/submitmsg.php";
	public static final int MESSAGE_REPORT_FILTER = 5;

	// Quiz
	public static final String QUIZ_RANKING = "http://paulshin.ca/yunatube/res/quiz/quiz_ranking.php?version=";

	// Game
	public static final String GAME_RANKING = "http://paulshin.ca/yunatube/res/game/game_ranking_2.php?nickname=%s#%s";
	public static final String GAME_SUBMIT = "http://paulshin.ca/yunatube/res/game/game_submit_2.php";

	// Chat
	public static final String CHAT_USERS = "http://paulshin.ca/yunatube/res/chat/chat_users.php";
	public static final String CHAT_DATA = "http://paulshin.ca/yunatube/res/chat/chat_data.php";
}
