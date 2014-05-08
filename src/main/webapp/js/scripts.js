// The root URL for the RESTful services
var rootURL = "http://localhost:8080/RESTful_NewsMavenized/rest/news";
var newsDetailURL = "nd.html";
var findtype = "";

// Read a page's GET URL variables and return them as an associative array.
$.extend({
	getUrlVars : function() {
		var vars = [], hash;
		var hashes = window.location.href.slice(
				window.location.href.indexOf('?') + 1).split('&');
		for (var i = 0; i < hashes.length; i++) {
			hash = hashes[i].split('=');
			vars.push(hash[0]);
			vars[hash[0]] = hash[1];
		}
		return vars;
	},
	getUrlVar : function(name) {
		return $.getUrlVars()[name];
	},
	findByID : function(newsID, typeFind) {
		findtype = typeFind;

		// console.log('findByID / findtype: ' + findtype + ' / rootURL: ' +
		// rootURL + '/' + newsID);
		$.ajax({
			type : 'GET',
			url : rootURL + '/' + newsID,
			dataType : "json", // data type of response
			success : $.renderNews
		});
	},
	findAll : function(typeFind) {
		findtype = typeFind;

		// console.log('findAll / findtype: ' + findtype + ' / rootURL: ' +
		// rootURL + '/all');
		$.ajax({
			type : 'GET',
			url : rootURL + '/all',
			dataType : "json", // data type of response
			success : $.renderNews
		});
	},
	renderNews : function(data) {

		// console.log('renderNews / findtype: ' + findtype + '');

		// JAX-RS serializes an empty list as null, and a 'collection of one' as
		// an object (not an 'array of one')
		var newss = data.news == null ? []
				: (data.news instanceof Array ? data.news : [ data.news ]);

		var htmlNewsArea = "";
		// console.log('renderNews / htmlNewsArea: >' + htmlNewsArea + '<');
		// debugger; // jquery-debugger

		$.each(newss, function(index, news) {

			var newDate = new Date(news.createdate).toLocaleDateString();
			var res = "";

			// console.log('renderNews / newDate: ' + newDate + '');
			// console.log('renderNews / news.newsid: ' + news.newsid + '');
			// console.log('renderNews / news.newsId: ' + news.newsId + '');
			// console.log('renderNews / news.title: ' + news.title + '');

			htmlNewsArea += '<p class="news-Area"><span class="bold">'
					+ newDate + '</span> &nbsp; <a href="' + newsDetailURL
					+ '?nid=' + news.newsid + '" data-identity="' + news.newsid
					+ '"><strong>' + news.title + '</strong></a></p>'
					+ '<div class="news-text"><strong>' + news.description
					+ '</strong><br/>';

			res = news.text;

			if (findtype == "findAll") {
				// Nachrichtentext auf 50 Zeichen kuerzen
				res = news.text.substring(0, 50);
			}

			htmlNewsArea += res;

			if (findtype == "findAll") {
				// "weiterlesen" nur anzeigen, wenn bei der Uebersicht
				htmlNewsArea += '... <a href="' + newsDetailURL + '?nid='
						+ news.newsid + '" data-identity="' + news.newsid
						+ '">weiterlesen' + '</a>';
			}

			htmlNewsArea += '</div><div class="clear"> &nbsp; </div>';

			// console.log('renderNews / htmlNewsArea: ' + htmlNewsArea + '');

		});
		$('#newsArea2').append(htmlNewsArea);
		htmlNewsArea = "";
		findtype = "";
	}
});