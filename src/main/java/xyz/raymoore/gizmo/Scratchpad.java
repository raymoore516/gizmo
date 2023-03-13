package xyz.raymoore.gizmo;

import xyz.raymoore.gizmo.element.body.AnchorElement;
import xyz.raymoore.gizmo.element.body.ContainerElement;
import xyz.raymoore.gizmo.element.body.HeaderElement;
import xyz.raymoore.gizmo.element.body.ParagraphElement;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used as a "scratchpad" and is constantly in flux.
 * TODO: Add this to .gitignore?
 */
public class Scratchpad {
    public static void main(String[] args) {
        ContainerElement content = new ContainerElement();
        content.setId("content");

        HeaderElement title = new HeaderElement(HeaderElement.Level.h1);
        title.setText("Bookmark List");
        content.appendChild(title);

        ContainerElement list = new ContainerElement();
        list.addClass("bookmark-list");
        content.appendChild(list);

        List<Bookmark> bookmarks = mockData();  // Mock database retrieval
        for (Bookmark bookmark : bookmarks) {
            AnchorElement a;
            ParagraphElement p;

            ContainerElement div = new ContainerElement();
            div.addClass("bookmark");
            list.appendChild(div);

            a = new AnchorElement();
            a.addClass("bold");
            a.setText("here");
            a.setReference(bookmark.URL);
            a.setTarget("_blank");

            // This demonstrates a mix & match of Gizmo '@@@' wildcard and Java '%s' formatting
            p = new ParagraphElement();
            p.setContent(String.format("Click @@@ to navigate to the %s website", bookmark.NAME), a);
            div.appendChild(p);

            p = new ParagraphElement();
            p.setText("This is another paragraph with no inline elements");
            div.appendChild(p);
        }

        Document html = new Document("Demo").init();
        html.setContent(content);

        System.out.println(html);
    }

    public static List<Bookmark> mockData() {
        List<Bookmark> data = new ArrayList<>();
        data.add(new Bookmark("Apple", "https://apple.com"));
        data.add(new Bookmark("Google", "http://google.com"));
        data.add(new Bookmark("Netflix", "http://netflix.com"));
        data.add(new Bookmark("<This> & <That>", "http://testcase.com"));

        return data;
    }

    public static class Bookmark {
        String NAME;
        String URL;

        Bookmark(String name, String url) {
            this.NAME = name;
            this.URL = url;
        }
    }
}