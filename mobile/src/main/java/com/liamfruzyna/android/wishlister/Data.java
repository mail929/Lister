package com.liamfruzyna.android.wishlister;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail929 on 2/24/17.
 */

public class Data {
    private static int current = 0;
    private static List<ListObj> lists = new ArrayList<>();

    //returns the list currently viewable on screen
    public static ListObj getCurrentList() {
        if(current == -1)
        {
            current = 0;
        }
        return getUnArchived().get(current);
    }

    //returns the name of the current selected list
    public static String getCurrentName() {
        return getCurrentList().name;
    }

    //returns the items currently displayed on screen
    public static List<Item> getItems() {
        return getCurrentList().items;
    }

    //returns all the lists
    public static List<ListObj> getLists() {
        return lists;
    }

    //returns all the unarchived lists
    public static List<ListObj> getUnArchived() {
        List<ListObj> unar = new ArrayList<>();
        for(ListObj list : lists)
        {
            if(!list.archived)
            {
                unar.add(list);
            }
        }
        return unar;
    }

    public static void setCurrent(int current) {
        Data.current = current;
    }

    public static int getCurrent() {
        return current;
    }

    public static List<String> getNames() {
        List<String> names = new ArrayList<>();
        for(ListObj list : getUnArchived())
        {
            names.add(list.name);
        }
        return names;
    }

    public static void setLists(List<ListObj> lists) {
        Data.lists = lists;
    }

    public static Item findItem(String itemValue)
    {
        List<Item> items = getCurrentList().items;
        for(Item item : items)
        {
            if(item.item.equals(itemValue))
            {
                return item;
            }
        }
        return null;
    }

    public static void setItems(List<Item> items) {
        Data.getCurrentList().items = items;
    }

    //takes the name of a list and returns the list object
    public static ListObj getListFromName(String name) {
        for (ListObj list : lists) {
            if (list.name.equals(name)) {
                return list;
            }
        }
        return null;
    }


    public static List<String> getTags() {
        List<String> tags = new ArrayList<>();
        for (ListObj list : lists) {
            if (!list.archived) {
                for (String tag : list.tags) {
                    boolean found = false;
                    for (String tagg : tags) {
                        if (tagg.toLowerCase().equals(tag.toLowerCase())) {
                            found = true;
                        }
                    }
                    if (!found && !tag.contains("@")) {
                        tags.add(tag);
                    }
                }
                for (Item item : list.items) {
                    for (String tag : item.tags) {
                        boolean found = false;
                        for (String tagg : tags) {
                            if (tag.toLowerCase().equals(tagg.toLowerCase())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            tags.add(tag);
                        }
                    }
                }
            }
        }
        return tags;
    }

    public static List<String> getPeopleTags() {
        List<String> people = new ArrayList<>();
        for (ListObj list : lists) {
            if (!list.archived) {
                for (Item item : list.items) {
                    for (String person : item.people) {
                        boolean found = false;
                        for (String personn : people) {
                            if (person.toLowerCase().equals(personn.toLowerCase())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            people.add(person);
                        }
                    }
                }
                for (String person : list.people) {
                    boolean found = false;
                    for (String sPerson : people) {
                        if (person.toLowerCase().equals(sPerson.toLowerCase())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        people.add(person);
                    }
                }

            }
        }
        return people;
    }

    public static void replaceList(ListObj list)
    {
        boolean found = false;
        for(int i = 0; i < lists.size(); i++)
        {
            if(lists.get(i).name.equals(list.name))
            {
                found = true;
                lists.set(i, list);
                break;
            }
        }
        if(!found)
        {
            lists.add(list);
        }
    }
}
