function img_replace(source, replaceSource) {
	var allImages = document.getElementsByTagName("img");
	var target;
	
	for(var i = 0, max = allImages.length; i < max; i++) {
	    if (allImages[i].getAttribute("zhimg-src") === source){
	       target = allImages[i];
	       break;
	    }
    }
    
    target.src = replaceSource;
}
