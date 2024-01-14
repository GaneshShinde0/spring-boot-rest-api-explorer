- Consider it as a comment section

table will save follwing fields

- id
- name
- type
- parent_id

- For Root consider parent_id as null
- for 1st level folders parent_id=0 as root has id of 0
- for all the next folders consider id of parent folder as parent id.
- For Fetching all folders and subfolders.
  - Fetch Root, Fetch all childrens of root folder.
  - Fetch childrens of all childrens recursively.
- For Creating Single Folder
  - With help of parent_id search if parent exists or not.
  - if parent exists save otherwise send response as parent does not exists.
- For Deleting
  - Get all the childrens delete them recursively.
  - Two ways to do this.
    - Delete all the records which have same parent id as of id of deleted one using one query/operation.
      - For file explorer use recursive delete.
    - Go to every leaf folder/file of that folder and delete them one by one.
	The first one looks easy


API EndPoints.

- GET: http://localhost:9090/api/explorer
- DELETE: http://localhost:9090/api/explorer/{id}
- POST: http://localhost:9090/api/explorer/{id}

POST BODY
	
	{
	  "name": "kk",
	  "type": "folder",
	  "parentId" : 1
	}
		

	Post API Body

Solution: 

	Database Used: H2
	Username: sa
	Password: password
	Port: 9090
	DatabaseUrl: http://localhost:9090/h2-console

Steps to run Jar:

java jar "# spring-boot-rest-api-explorer" 
"# spring-boot-rest-api-explorer" 
