<?xml version="1.0" encoding="UTF-8"?>
<ddl generateComments="true" generateDrops="true" exportDate="2011-03-10"
     exportTime="13:11:19">
   <model name="PartsSourceA" uuid="mmuuid:8fb3de1d-e4d4-41f6-807f-c607e6527d30"
          modelFilename="/PartsProject/PartsSourceA.xmi"
          modelType="Physical"
          metaModel="relational"
          metaModelURL="http://www.metamatrix.com/metamodels/Relational">
      <table name="PARTS" uuid="mmuuid:614d68e2-8a2c-4b2f-b811-d10b861be56e"
             pathInModel="PARTS">
         <column name="PART_ID" uuid="mmuuid:1ec4c096-46a6-494b-9cb4-0efdf213a06f"
                 type="varchar"
                 length="50"
                 isLengthFixed="false"
                 isNullable="false"
                 supportsSelect="true"
                 supportsUpdate="true"
                 isCaseSensitive="true"
                 isSigned="true"
                 isCurrency="false"
                 isAutoIncremented="false"/>
         <column name="PART_NAME" uuid="mmuuid:2e3fa539-2ff4-496f-90dd-5e43f2d3869b"
                 type="varchar"
                 length="255"
                 isLengthFixed="false"
                 isNullable="true"
                 supportsSelect="true"
                 supportsUpdate="true"
                 isCaseSensitive="true"
                 isSigned="true"
                 isCurrency="false"
                 isAutoIncremented="false"/>
         <column name="PART_COLOR" uuid="mmuuid:2f5763be-b782-4db1-b8a4-9585d771f7ae"
                 type="varchar"
                 length="30"
                 isLengthFixed="false"
                 isNullable="true"
                 supportsSelect="true"
                 supportsUpdate="true"
                 isCaseSensitive="true"
                 isSigned="true"
                 isCurrency="false"
                 isAutoIncremented="false"/>
         <column name="PART_WEIGHT" uuid="mmuuid:b32b6c3c-7d8d-41fd-befd-3a10a6fe7ddb"
                 type="varchar"
                 length="255"
                 isLengthFixed="false"
                 isNullable="true"
                 supportsSelect="true"
                 supportsUpdate="true"
                 isCaseSensitive="true"
                 isSigned="true"
                 isCurrency="false"
                 isAutoIncremented="false"/>
      </table>
   </model>
</ddl>