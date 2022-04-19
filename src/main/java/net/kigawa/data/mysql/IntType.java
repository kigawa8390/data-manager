package net.kigawa.data.mysql;

import net.kigawa.data.database.SqlDataType;
import net.kigawa.data.javadata.IntData;
import net.kigawa.data.javadata.JavaData;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @deprecated
 */
public class IntType extends SqlDataType<IntData>
{

    @Override
    public IntData getData(String key, ResultSet resultSet) throws SQLException
    {
        return new IntData(resultSet.getInt(key));
    }

    @Override
    public String getSql()
    {
        return "INT";
    }

    @Override
    public boolean isAllow(JavaData javaData)
    {
        return javaData instanceof IntData;
    }

    @Override
    public boolean equals(String name)
    {
        return getSql().equals(name);
    }
}
