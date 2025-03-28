package com.rivensoftware.hardcoresmp.tools;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;


public class InventorySerializer
{

    public static String serialize(ItemStack[] items) throws IllegalStateException 
    {
        // This contains contents, armor and offhand (contents are indexes 0 - 35, armor 36 - 39, offhand - 40)
        return itemStackArrayToBase64(items);
    }

    /**
     *
     * A method to serialize an {@link ItemStack} array to Base64 String.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException 
    {
        try 
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) 
            {
                if (item != null) 
                {
                    dataOutput.writeObject(item.serialize());
                } 
                else 
                {
                    dataOutput.writeObject(null);
                }
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } 
        catch (Exception e) 
        {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
	public static ItemStack[] deserialize(String data) throws IOException 
    {
        try 
        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int Index = 0; Index < items.length; Index++) 
            {
                Map<String, Object> stack = (Map<String, Object>) dataInput.readObject();

                if (stack != null) 
                {
                    items[Index] = ItemStack.deserialize(stack);
                }
                else 
                {
                    items[Index] = null;
                }
            }

            dataInput.close();
            return items;
        } 
        catch (ClassNotFoundException e) 
        {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}