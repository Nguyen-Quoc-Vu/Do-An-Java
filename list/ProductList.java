import java.util.LinkedList;
import java.lang.NoSuchMethodException;
import java.lang.SecurityException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import java.lang.IllegalArgumentException;
import java.lang.reflect.InvocationTargetException;

public abstract class ProductList<T extends Product> implements IPrintable
{
    /*Member*/
    protected LinkedList<T> list;
    protected PublisherList publisherList;
    public String path;

    /*Get methods*/
    public T getProduct(String id)
    {
        int index = findProduct(id);
        if (index == -1)
        {
            System.out.println("Cannot find ID!");
            return null;
        }
        else
        {
            return list.get(index);
        }
    }

    public int getTotalQuantity() {
        return list.size();
    }

    public int getQuantity(String id) {
        int index = findProduct(id);
        int count = 0;
        if (index == -1)
        {
            System.out.println("Cannot find ID!");
            return 0;
        }
        else
        {
            for (T product : list)
                if (product.getID().equals(id))
                    ++count;
        }

        return count;
    }

    /*Set methods*/

    /*Other methods*/
    public void addProduct(String id, int quantity, Class<T> tclass) {
        int index = findProduct(id);
        if (index == -1)
        {
            System.out.println("This is new ID!");
            try
            {
                T newProduct = (T)tclass.getConstructor(String.class).newInstance(id);

                if (!publisherList.checkPublisherList(newProduct))
                {
                    System.out.println("Failed! Invalid information!");
                    return;
                }

                list.add(newProduct);
                FileIO.writeToFile(newProduct, path);
                for (int i = 0; i < quantity - 1; ++i)
                {
                    T product = (T)tclass.getConstructor(tclass).newInstance(newProduct);
                    list.add(product);
                    FileIO.writeToFile(product, path);
                }
            }
            catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException |InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            T existProduct = getProduct(id);
            for (int i = 0; i < quantity; ++i)
            {
                try
                {
                    T product = (T)tclass.getConstructor(tclass).newInstance(existProduct);
                    list.add(product);
                    FileIO.writeToFile(product, path);
                }
                catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException |InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public abstract String toString();

    public void removeProduct(String id, int quantity) {
        int index = findProduct(id);
        if (index == -1)
        {
            System.out.println("Failed: Cannot find ID!");
            return;
        }
        else
        {
            for (int i = 0; i < quantity; ++i)
            {
                if (index == -1)
                    break;
                    
                list.remove(index);
                index = findProduct(id);
            }
        }

        FileIO.rewriteFile(list, path);
    }

    public void removeAllProduct(String id) {
        int index = findProduct(id);
        if (index == -1)
        {
            System.out.println("Failed: Cannot find ID!");
            return;
        }
        else
        {
            while (index != -1)
            {
                list.remove(index);
                index = findProduct(id);
            }
        }

        FileIO.rewriteFile(list, path);
    }

    public int findProduct(String id) {
        for (T product : list)
            if (product.getID().equals(id))
                return list.indexOf(product);

        return -1;
    }
}