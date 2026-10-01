package com.example.ui.browser

/** JavaScript bridge overlay for captions while the site's own video player remains active. */
object WebSubtitleOverlay {
    val script = """
        (function() {
          if (window.__omniSubtitleOverlayInstalled) return;
          window.__omniSubtitleOverlayInstalled = true;
          var enabled = true, seq = 0, lastText = '';
          var box = document.createElement('div');
          box.id = '__omni_translated_subtitle';
          box.style.cssText = 'position:fixed;z-index:2147483647;left:8%;right:8%;bottom:12%;display:none;opacity:0;transition:opacity .18s ease;text-align:center;pointer-events:none;font-family:sans-serif;font-size:20px;font-weight:700;color:#fff;background:rgba(0,0,0,.78);border-radius:8px;padding:8px 12px;text-shadow:0 1px 2px #000;direction:auto;';
          (document.body || document.documentElement).appendChild(box);
          var activeVideo = null;
          function place() {
            var v = activeVideo || document.querySelector('video');
            if (!v) return;
            var r = v.getBoundingClientRect();
            if (!r.width || !r.height) return;
            box.style.left = Math.max(8, r.left + r.width * .08) + 'px';
            box.style.width = Math.max(80, r.width * .84) + 'px';
            box.style.right = 'auto';
            box.style.bottom = 'auto';
            box.style.top = Math.max(8, r.bottom - Math.min(130, r.height * .22)) + 'px';
          }
          function show(text) { box.textContent=text||''; if (text) { box.style.display='block'; requestAnimationFrame(function(){box.style.opacity='1';}); } else { box.style.opacity='0'; setTimeout(function(){if(box.style.opacity==='0') box.style.display='none';},180); } }
          window.OmniSubtitleSetEnabled = function(v) { enabled = !!v; if (!enabled) show(''); };
          window.OmniSubtitleSetTranslated = function(id, text) {
            if (!enabled || id !== seq) return;
            show(text);
          };
          window.OmniSubtitleSetLive = function(text) {
            if (!enabled) return;
            show(text);
          };
          window.OmniApplyPageTranslation = function(raw) {
            try {
              var map = JSON.parse(raw || '{}');
              var w = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT);
              while (w.nextNode()) {
                var n=w.currentNode,p=n.parentElement;
                if(!p||/^(SCRIPT|STYLE|NOSCRIPT|INPUT|TEXTAREA|SELECT|BUTTON)$/.test(p.tagName)) continue;
                var original=(n.nodeValue||'').trim(), translated=map[original];
                if(translated && !p.hasAttribute('data-omni-original')) { p.setAttribute('data-omni-original',p.textContent||''); n.nodeValue=(n.nodeValue||'').replace(original,translated); }
              }
            } catch(e) {}
          };
          function send(text, start, end) {
            text = (text || '').replace(/<[^>]*>/g,'').trim();
            if (!enabled || !text || text === lastText) return;
            lastText = text; seq++;
            try { window.OmniBridge && window.OmniBridge.onSubtitleCue(text, start, end, seq); } catch(e) {}
          }
          function inspect() {
            var v = document.querySelector('video');
            if (!v) return;
            activeVideo = v;
            place();
            var tracks = v.textTracks || [];
            for (var i=0; i<tracks.length; i++) {
              try { tracks[i].mode = 'hidden'; } catch(e) {}
              tracks[i].oncuechange = function() {
                var active = this.activeCues && this.activeCues[0];
                if (active) send(active.text, Math.round(active.startTime*1000), Math.round(active.endTime*1000));
                else { lastText=''; show(''); }
              };
            }
            if (!v.__omniTimeHook) {
              v.__omniTimeHook = true;
              v.addEventListener('timeupdate', function() {
                for (var j=0; j<(v.textTracks||[]).length; j++) {
                  var a=v.textTracks[j].activeCues;
                  if (a && a.length) { var c=a[0]; send(c.text, Math.round(c.startTime*1000), Math.round(c.endTime*1000)); return; }
                }
              });
            }
          }
          document.addEventListener('fullscreenchange', function() {
            var host = document.fullscreenElement || document.body || document.documentElement;
            if (box.parentNode !== host) host.appendChild(box);
            inspect(); place();
          });
          window.addEventListener('resize', place);
          window.addEventListener('scroll', place, {passive:true});
          inspect(); place();
          new MutationObserver(inspect).observe(document.documentElement,{childList:true,subtree:true});
          setInterval(inspect, 1500);
        })();
    """.trimIndent()
}
