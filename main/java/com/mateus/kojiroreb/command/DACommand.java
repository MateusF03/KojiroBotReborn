package com.mateus.kojiroreb.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class DACommand implements EventListener {


    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            Message message = ((MessageReceivedEvent) event).getMessage();
            String messageContent = message.getContentRaw();
            TextChannel textChannel = ((MessageReceivedEvent) event).getTextChannel();
            if (messageContent.startsWith("_deviantart")) {
                String[] args = messageContent.split(" ");
                if (args.length < 2) {
                    textChannel.sendMessage("Você não colocou argumentos suficientes!").queue();
                } else {
                    String search;
                    if(args.length > 2) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 1; i<args.length; i++) {
                            stringBuilder.append(args[i]).append("%20");
                        }
                        search = stringBuilder.toString();
                    } else {
                        search = args[1];
                    }
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    try {
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(new URL("https://backend.deviantart.com/rss.xml?type=deviation&q=boost%3Apopular%2Fdrawings+" + search).openStream());
                        Element docEle = doc.getDocumentElement();
                        NodeList nodeList = docEle.getChildNodes();
                        List<String> arts = new ArrayList<>();
                        System.out.println(nodeList.getLength());
                        NodeList childList = null;
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                                Element el = (Element) nodeList.item(i);
                                if (el.getNodeName().contains("channel")) {
                                    childList = el.getChildNodes();
                                    break;
                                }
                            }
                        }
                        for (int i = 0; i<childList.getLength(); i++) {
                            if (childList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                                Element el = (Element) childList.item(i);
                                if (el.getNodeName().contains("item")) {
                                    String author = el.getElementsByTagName("media:credit").item(0).getTextContent();
                                    String url = el.getElementsByTagName("media:content").item(0).getAttributes().getNamedItem("url").getNodeValue();
                                    String title = el.getElementsByTagName("media:title").item(0).getTextContent();
                                    String rating = el.getElementsByTagName("media:rating").item(0).getTextContent();
                                    arts.add(author+"%10"+title+"%10"+url+"%10"+rating);
                                }
                            }
                        }
                        System.out.println(arts.size());
                        String[] randomArt = arts.get(new Random().nextInt(arts.size())).split("%10");
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setColor(Color.RED);
                        String[] lewd = {
                                "https://cdn.discordapp.com/attachments/363440758411231252/632629546373611550/zAp2LzJ.png",
                                "https://cdn.discordapp.com/attachments/363440758411231252/632629599800524834/9k.png",
                                "https://cdn.discordapp.com/attachments/363440758411231252/632629704788017157/cq9irini0ex21.png"
                        };
                        if (randomArt[3].equals("adult")) {
                            if (!textChannel.isNSFW()) {
                                eb.setAuthor("**Opa**");
                                eb.setImage(lewd[new Random().nextInt(lewd.length)]);
                                eb.addField("LEWD!", "A arte é NSFW, ela só pode ser enviada em canais NSFW", true);
                            } else {
                                eb.setAuthor("Resultado de pesquisa de: " + search.replaceAll("%20", " "));
                                eb.setTitle(randomArt[1]);
                                eb.setFooter("Criado por: " + randomArt[0]);
                                eb.setImage(randomArt[2]);
                            }
                        } else {
                            eb.setAuthor("Resultado de pesquisa de: " + search.replaceAll("%20", " "));
                            eb.setTitle(randomArt[1]);
                            eb.setFooter("Criado por: " + randomArt[0]);
                            eb.setImage(randomArt[2]);
                        }
                        textChannel.sendMessage(eb.build()).queue();


                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }
                }
            } else if (messageContent.equalsIgnoreCase("desliga")) {
                if (!message.getAuthor().getId().equals("175653597982359552")) return;
                event.getJDA().shutdown();
                System.exit(0);
            }else if (messageContent.equalsIgnoreCase("_teste")) {
                textChannel.sendMessage("olá").queue();
            }
        }
    }
}
