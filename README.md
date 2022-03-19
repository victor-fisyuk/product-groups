# Product Groups

Demonstrates how to use Optimistic Locking in Hibernate.

The app includes:
* **Products** (such as _Video Card_, _Processor_, _Motherboard_, etc.).
* **Groups** (such as _Computers_) that contain products.
* **Tags** that can be assigned to groups and that are automatically _inherited_ to the group products.
* API for adding a product to a group
* API for assigning a tag to a group

Optimistic Locking is used to properly handle concurrent transactions of adding products to groups and assigning tags to groups.
