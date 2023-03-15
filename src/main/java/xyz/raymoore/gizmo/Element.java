package xyz.raymoore.gizmo;

import java.util.*;

public abstract class Element implements Renderable {
    public enum Type {
        block,
        inline
    }

    // --------------------
    //  Fields
    // --------------------

    public static final int BLOCK_ELEMENT_PADDING_CHARS = 2;

    protected Type type;
    protected String tag;
    protected boolean isVoid;  // Void elements do not have a closing tag
    protected Map<String, String> attributes;

    protected List<Element> children;  // List of child elements
    protected List<Content> content;  // List of inline elements and/or raw text

    // --------------------
    //  Constructors
    // --------------------

    public Element(Type type, String tag) {
        this.type = type;
        this.tag = tag;
        this.attributes = new TreeMap<>();
        this.children = new ArrayList<>();
        this.content = new ArrayList<>();
    }

    // --------------------
    //  Methods
    // --------------------

    public Type getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    public void appendChild(Element element) {
        children.add(element);
    }

    public void setAttribute(String key) {
        attributes.put(key, null);  // Used for 'checked', 'hidden', etc.
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public void addElement(Element element) {
        if (this.getType() == Type.inline && element.getType() == Type.block) {
            String msg = String.format("Cannot add block '%s' tag to inline '%s' tag", element.getTag(), tag);
            throw new IllegalArgumentException(msg);
        }

        this.content.add(new Content(element));
    }

    public void addText(String text) {
        this.content.add(new Content(text));
    }

    public void addText(String format, Object... args) {
        addText(String.format(format, args));
    }

    public void setText(String text) {
        this.content = Collections.singletonList(new Content(text));
    }

    public void setText(String format, Object... args) {
        setText(String.format(format, args));
    }

    /**
     * Global HTML Attributes
     */

    public void setId(String name) {
        setAttribute("id", name);
    }

    public void addClass(String... classes) {
        for (String cls : classes) {
            if (attributes.get("class") == null) {
                setAttribute("class", cls);
            } else {
                setAttribute("class", String.format("%s %s", attributes.get("class"), cls));
            }
        }
    }

    public void setClassList(String... classes) {
        StringBuilder builder = new StringBuilder();
        for (String cls : classes) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(cls);
        }

        setAttribute("class", builder.toString());
    }

    public void setData(String key, String value) {
        // Convert "camelCase" key to "kebab-case" key
        key = key.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase();

        setAttribute(String.format("data-%s", key), value);
    }

    public void setHidden() {
        setAttribute("hidden");
    }

    /**
     * This returns the HTML as a string
     * @return HTML content
     */

    public String render(int padding) {
        StringBuilder sb = new StringBuilder();

        // Start tag
        sb.append(" ".repeat(padding));
        sb.append("<");
        sb.append(tag);
        for (String key : attributes.keySet()) {
            // Insert tag attributes
            sb.append(" ");
            sb.append(key);
            if (attributes.get(key) == null) {
                continue;  // Used for 'checked', 'hidden', etc.
            }
            sb.append("=\"");
            sb.append(attributes.get(key));
            sb.append("\"");
        }
        sb.append(">");

        // Handle void elements
        if (isVoid) {
            return sb.toString();
        }

        if (children.size() > 0) {
            // Render children and handle padding
            for (Element child : children) {
                sb.append('\n');
                sb.append(child.render(padding + BLOCK_ELEMENT_PADDING_CHARS));
            }
            sb.append('\n');
            sb.append(" ".repeat(padding));
        } else {
            // Add inner content of inline elements and raw text
            for (Content c : content) {
                if (content.size() > 1) {
                    sb.append('\n');
                    sb.append(" ".repeat(padding + BLOCK_ELEMENT_PADDING_CHARS));
                }
                if (c.getType() == Content.Type.inline) {
                    sb.append(c.getElement().render(0));
                    continue;
                }
                // @formatter:off
                String innerHTML = c.getText()
                            .replace("&", "&amp;")  // REPLACE '&' FIRST!
                            .replace("<", "&lt;")
                            .replace(">", "&gt;");
                // @formatter:on
                sb.append(innerHTML);
            }
            if (content.size() > 1) {
                sb.append('\n');
                sb.append(" ".repeat(padding));
            }
        }

        // Close tag
        sb.append("</");
        sb.append(tag);
        sb.append(">");

        return sb.toString();
    }

    @Override
    public String toString() {
        return render(0);
    }

    // --------------------
    //  Inner Classes
    // --------------------

    public static class Content {
        public enum Type {
            inline,
            rawText
        }

        private final Type type;
        private final Element element;
        private final String text;

        Content(Element element) {
            this.type = Type.inline;
            this.element = element;
            this.text = null;
        }

        Content(String text) {
            this.type = Type.rawText;
            this.element = null;
            this.text = text;
        }

        public Type getType() {
            return type;
        }

        public Element getElement() {
            return element;
        }

        public String getText() {
            return text;
        }
    }
}
