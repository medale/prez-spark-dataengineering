# prez-spark-dataengineering
Repository for Data Engineering with Apache Spark for Baltimore Scala Meetup, May 2nd, 2019.

# Exploring via Jupyter with Apache Toree Scala notebooks

* Copy notebooks from *prez-spark-dataengineering/notebooks* to */home/jovyan/work* to make them available from Docker notebook.

* See Docker documentation at https://docs.docker.com/install/ for install on your OS
* We will be using jupyter/all-spark-notebook:14fdfbf9cfc1 which ran Spark 2.4.0 as of Nov 28, 2018
     * The latest 2.4.2 notebook has class not found error in Toree (on 28APR19, ae5f7e104dd5)
     * Adjust your local Spark standalone to match the all-spark-notebook version *or*
     * Check out https://github.com/jupyter/docker-stacks and adjust pyspark-notebook Spark/Hadoop and then load from adjusted all-spark-notebook locally.
* The bash script below assumes that:
     * `SPARK_HOME` environment variable points to the base of your Spark installation
     * `192.168.2.8` adjust to your local IP address (don't use `localhost` - non-routable from Docker container)
* Shared local directories with same dirs on host machine as in Docker image
     * `/dataset/github/data` - is the local dir containing your data files (e.g. 2019-04-28-0.json.gz)
          * Spark driver runs on Docker machine
          * Executor runs on Docker host machine
     * `/home/jovyan/work` - directory containing notebook(s) to load (Jupyter notebook on Docker image runs from `/home/jovyan`)

```bash
# Using 2.4.0 spark notebook due to class not found in latest 2.4.2 version
docker pull jupyter/all-spark-notebook:14fdfbf9cfc1

# show all downloaded images
docker images
# delete an image
docker rmi <image-id>

# For "local" standalone Spark cluster with master/executor on local machine
# Download Spark 2.4.2 for Hadoop 2.7 from https://spark.apache.org/downloads.html
# Untar, set $SPARK_HOME to spark-2.4.2-bin-hadoop2.7 dir
$SPARK_HOME/sbin/start-master.sh --host 192.168.2.8
$SPARK_HOME/sbin/start-slave.sh spark://192.168.2.8:7077

# See "Connecting to a Spark Cluster in Standalone Mode" at
# https://jupyter-docker-stacks.readthedocs.io/en/latest/using/specifics.html#apache-spark
docker run -p 8888:8888 -v /datasets/github:/datasets/github \
   -v /home/jovyan/work:/home/jovyan/work \
   --net=host --pid=host -e TINI_SUBREAPER=true \
   -e SPARK_OPTS='--master=spark://192.168.2.8:7077 --executor-memory=8g' \
   jupyter/all-spark-notebook:14fdfbf9cfc1
```
