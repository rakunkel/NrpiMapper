function cmv(){var M='',oc='\n-',ub='" for "gwt:onLoadErrorFn"',sb='" for "gwt:onPropertyErrorFn"',bc='"<script src=\\"',fb='"><\/script>',W='#',nc=');',fc='-\n',pc='-><\/scr',cc='.cache.js\\"><\/scr" + "ipt>"',Y='/',ib='//',Jb='25A4E4F6340552AB2F3537AF9E427851',Kb='56AE7F3CA76AC518E83B3042AD85A30C',Lb='56F7432181E9531A95891A9C6C4E31EC',Mb='7CCBF17CDD9F3BBFFAAA6F7FFAD2C8B5',Pb=':',mb='::',dc='<scr',eb='<script id="',ac='<script language="javascript" src="http://serverapi.arcgisonline.com/jsapi/arcgis/?v=2.8"><\/script>',pb='=',X='?',Nb='A36AD41F7314F66A1C815FD9F956C6FA',rb='Bad handler "',Yb='CeresMapViewer.css',Gb='Cross-site hosted mode not yet implemented. See issue ',Zb='DOMContentLoaded',Ob='F3A7698E380D3CA1DA90CB2934A4995C',Wb='GwtGisClient.css',gb='SCRIPT',db='__gwt_marker_cmv',hb='base',_='baseUrl',Q='begin',P='bootstrap',$='clear.cache.gif',N='cmv',bb='cmv.nocache.js',lb='cmv::',ob='content',mc='document.write(',V='end',ic='evtGroup: "loadExternalRefs", millis:(new Date()).getTime(),',kc='evtGroup: "moduleStartup", millis:(new Date()).getTime(),',Db='gecko',Eb='gecko1_8',R='gwt.codesvr=',S='gwt.hosted=',T='gwt.hybrid',Xb='gwt/standard/standard.css',tb='gwt:onLoadErrorFn',qb='gwt:onPropertyErrorFn',nb='gwt:property',Vb='head',Ub='href',Hb='http://code.google.com/p/google-web-toolkit/issues/detail?id=2079',Qb='http://serverapi.arcgisonline.com/jsapi/arcgis/2.8/js/dojo/dijit/themes/tundra/tundra.css',_b='http://serverapi.arcgisonline.com/jsapi/arcgis/?v=2.8',Cb='ie6',Bb='ie8',Ab='ie9',Z='img',qc='ipt>',ec='ipt><!-',Rb='link',$b='loadExternalRefs',jb='meta',hc='moduleName:"cmv", sessionId:window.__gwtStatsSessionId, subSystem:"startup",',U='moduleStartup',zb='msie',kb='name',wb='opera',Sb='rel',yb='safari',ab='script',Ib='selectingPermutation',O='startup',Tb='stylesheet',jc='type: "end"});',lc='type: "moduleRequested"});',cb='undefined',Fb='unknown',vb='user.agent',xb='webkit',gc='window.__gwtStatsEvent && window.__gwtStatsEvent({';var m=window,n=document,o=m.__gwtStatsEvent?function(a){return m.__gwtStatsEvent(a)}:null,p=m.__gwtStatsSessionId?m.__gwtStatsSessionId:null,q,r,s=M,t={},u=[],v=[],w=[],x=0,y,z;o&&o({moduleName:N,sessionId:p,subSystem:O,evtGroup:P,millis:(new Date).getTime(),type:Q});if(!m.__gwt_stylesLoaded){m.__gwt_stylesLoaded={}}if(!m.__gwt_scriptsLoaded){m.__gwt_scriptsLoaded={}}function A(){var b=false;try{var c=m.location.search;return (c.indexOf(R)!=-1||(c.indexOf(S)!=-1||m.external&&m.external.gwtOnLoad))&&c.indexOf(T)==-1}catch(a){}A=function(){return b};return b}
function B(){if(q&&r){q(y,N,s,x);o&&o({moduleName:N,sessionId:p,subSystem:O,evtGroup:U,millis:(new Date).getTime(),type:V})}}
function C(){function e(a){var b=a.lastIndexOf(W);if(b==-1){b=a.length}var c=a.indexOf(X);if(c==-1){c=a.length}var d=a.lastIndexOf(Y,Math.min(c,b));return d>=0?a.substring(0,d+1):M}
function f(a){if(a.match(/^\w+:\/\//)){}else{var b=n.createElement(Z);b.src=a+$;a=e(b.src)}return a}
function g(){var a=E(_);if(a!=null){return a}return M}
function h(){var a=n.getElementsByTagName(ab);for(var b=0;b<a.length;++b){if(a[b].src.indexOf(bb)!=-1){return e(a[b].src)}}return M}
function i(){var a;if(typeof isBodyLoaded==cb||!isBodyLoaded()){var b=db;var c;n.write(eb+b+fb);c=n.getElementById(b);a=c&&c.previousSibling;while(a&&a.tagName!=gb){a=a.previousSibling}if(c){c.parentNode.removeChild(c)}if(a&&a.src){return e(a.src)}}return M}
function j(){var a=n.getElementsByTagName(hb);if(a.length>0){return a[a.length-1].href}return M}
function k(){var a=n.location;return a.href==a.protocol+ib+a.host+a.pathname+a.search+a.hash}
var l=g();if(l==M){l=h()}if(l==M){l=i()}if(l==M){l=j()}if(l==M&&k()){l=e(n.location.href)}l=f(l);s=l;return l}
function D(){var b=document.getElementsByTagName(jb);for(var c=0,d=b.length;c<d;++c){var e=b[c],f=e.getAttribute(kb),g;if(f){f=f.replace(lb,M);if(f.indexOf(mb)>=0){continue}if(f==nb){g=e.getAttribute(ob);if(g){var h,i=g.indexOf(pb);if(i>=0){f=g.substring(0,i);h=g.substring(i+1)}else{f=g;h=M}t[f]=h}}else if(f==qb){g=e.getAttribute(ob);if(g){try{z=eval(g)}catch(a){alert(rb+g+sb)}}}else if(f==tb){g=e.getAttribute(ob);if(g){try{y=eval(g)}catch(a){alert(rb+g+ub)}}}}}}
function E(a){var b=t[a];return b==null?null:b}
function F(a,b){var c=w;for(var d=0,e=a.length-1;d<e;++d){c=c[a[d]]||(c[a[d]]=[])}c[a[e]]=b}
function G(a){var b=v[a](),c=u[a];if(b in c){return b}var d=[];for(var e in c){d[c[e]]=e}if(z){z(a,d,b)}throw null}
v[vb]=function(){var b=navigator.userAgent.toLowerCase();var c=function(a){return parseInt(a[1])*1000+parseInt(a[2])};if(function(){return b.indexOf(wb)!=-1}())return wb;if(function(){return b.indexOf(xb)!=-1}())return yb;if(function(){return b.indexOf(zb)!=-1&&n.documentMode>=9}())return Ab;if(function(){return b.indexOf(zb)!=-1&&n.documentMode>=8}())return Bb;if(function(){var a=/msie ([0-9]+)\.([0-9]+)/.exec(b);if(a&&a.length==3)return c(a)>=6000}())return Cb;if(function(){return b.indexOf(Db)!=-1}())return Eb;return Fb};u[vb]={gecko1_8:0,ie6:1,ie8:2,ie9:3,opera:4,safari:5};cmv.onScriptLoad=function(a){cmv.onScriptLoad=null;q=a;B()};if(A()){alert(Gb+Hb);return}D();C();o&&o({moduleName:N,sessionId:p,subSystem:O,evtGroup:P,millis:(new Date).getTime(),type:Ib});var H;try{F([Eb],Jb);F([Ab],Kb);F([Cb],Lb);F([yb],Mb);F([Bb],Nb);F([wb],Ob);H=w[G(vb)];var I=H.indexOf(Pb);if(I!=-1){x=Number(H.substring(I+1));H=H.substring(0,I)}}catch(a){return}var J;function K(){if(!r){r=true;if(!__gwt_stylesLoaded[Qb]){var a=n.createElement(Rb);__gwt_stylesLoaded[Qb]=a;a.setAttribute(Sb,Tb);a.setAttribute(Ub,Qb);n.getElementsByTagName(Vb)[0].appendChild(a)}if(!__gwt_stylesLoaded[Wb]){var a=n.createElement(Rb);__gwt_stylesLoaded[Wb]=a;a.setAttribute(Sb,Tb);a.setAttribute(Ub,s+Wb);n.getElementsByTagName(Vb)[0].appendChild(a)}if(!__gwt_stylesLoaded[Xb]){var a=n.createElement(Rb);__gwt_stylesLoaded[Xb]=a;a.setAttribute(Sb,Tb);a.setAttribute(Ub,s+Xb);n.getElementsByTagName(Vb)[0].appendChild(a)}if(!__gwt_stylesLoaded[Yb]){var a=n.createElement(Rb);__gwt_stylesLoaded[Yb]=a;a.setAttribute(Sb,Tb);a.setAttribute(Ub,s+Yb);n.getElementsByTagName(Vb)[0].appendChild(a)}B();if(n.removeEventListener){n.removeEventListener(Zb,K,false)}if(J){clearInterval(J)}}}
if(n.addEventListener){n.addEventListener(Zb,function(){K()},false)}var J=setInterval(function(){if(/loaded|complete/.test(n.readyState)){K()}},50);o&&o({moduleName:N,sessionId:p,subSystem:O,evtGroup:P,millis:(new Date).getTime(),type:V});o&&o({moduleName:N,sessionId:p,subSystem:O,evtGroup:$b,millis:(new Date).getTime(),type:Q});if(!__gwt_scriptsLoaded[_b]){__gwt_scriptsLoaded[_b]=true;document.write(ac)}var L=bc+s+H+cc;n.write(dc+ec+fc+gc+hc+ic+jc+gc+hc+kc+lc+mc+L+nc+oc+pc+qc)}
cmv();